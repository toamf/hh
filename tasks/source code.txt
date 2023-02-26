namespace Test;

/*
 * Приложение общается с удаленным сервисом: шлет запросы и получает ответы. С удаленным сервером
 * установлено единственное соединение, по которому мы шлем запросы и получаем ответы. Каждый запрос содержит Id (GUID),
 * ответ на запрос содержит его же. Ответы на запросы могут приходить в произвольном порядке и с произвольными задержками.
 * Необходимо реализовать интерфейс, который абстрагирует факт такого мультиплексирования.
 * Реализация `IRequestProcessor.Send` обязана быть потокобезопасной.
 */

// запрос, остальные поля не интересны
public sealed record Request(Guid Id);

// ответ, остальные поля не интересны
public sealed record Response(Guid Id); 

// низкоуровневый адаптер, можно делать одновременный вызов Get и Post
// можно считать это абстракцией над полнодуплексным сокетом
public interface IAdapter
{
    // вычитывает очередной ответ, нельзя делать несколько одновременных вызовов Get
    Task<Response> Get(CancellationToken cancellationToken);

    // отправляет запрос, нельзя делать несколько одновременных вызовов Post
    Task Post(Request request, CancellationToken cancellationToken);
}

// интерфейс, который надо реализовать
public interface IRequestProcessor
{
    // Запускает обработчик, возвращаемый Task завершается после окончания инициализации
    // гарантированно вызывается 1 раз при инициализации приложения
    Task Start(CancellationToken cancellationToken);

    // выполняет мягкую остановку, т.е. завершается после завершения обработки всех запросов
    // гарантированно вызывается 1 раз при остановке приложения
    Task Stop(CancellationToken cancellationToken);

    // выполняет запрос, этот метод будет вызываться в приложении множеством потоков одновременно
    // При отмене CancellationToken не обязательно гарантировать то, что мы не отправим запрос на сервер, но клиент должен получить отмену задачи
    Task<Response> Send(Request request, CancellationToken cancellationToken);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////


// сложный вариант задачи:
// 1. можно пользоваться только IAdapter
// 2. нужно реализовать обработку cancellationToken
// 3. нужно реализовать Stop, который дожидается получения ответов на уже переданные
//    запросы (пока не отменен переданный в `Stop` `CancellationToken`)
// 4. нужно реализовать настраиваемый таймаут: если ответ на конкретный запрос не получен за заданный промежуток
//    времени - отменяем задачу, которая вернулась из `Send`. В том числе надо рассмотреть ситуацию,
//    что ответ на запрос не придет никогда, глобальный таймаут при этом должен отработать и не допустить утечки памяти
public sealed class Processor : IRequestProcessor
{
    private readonly IAdapter _networkAdapter;
    private readonly TimeSpan _requestTimeout;
    private readonly ConcurrentDictionary<Guid, TaskCompletionSource<Response>> _pendingRequests = new();

    public Processor(IAdapter networkAdapter, TimeSpan requestTimeout)
    {
        if (requestTimeout <= TimeSpan.Zero)
            throw new ArgumentOutOfRangeException(nameof(requestTimeout));

        _networkAdapter = networkAdapter;
        _requestTimeout = requestTimeout;
    }

    public async Task Start(CancellationToken cancellationToken)
    {
        while (!cancellationToken.IsCancellationRequested)
        {
            var response = await _networkAdapter.Get(cancellationToken);
            if (_pendingRequests.TryRemove(response.Id, out var tcs))
            {
                tcs.TrySetResult(response);
            }
        }
    }

    public async Task Stop(CancellationToken cancellationToken)
    {
        // Сигнал о прекращении прослушивания входящих ответов
        cancellationToken.ThrowIfCancellationRequested();

        // Дождитесь завершения всех ожидающих запросов
        while (_pendingRequests.Count > 0)
        {
            var kvp = _pendingRequests.First();
            var tcs = kvp.Value;

            _pendingRequests.TryRemove(kvp.Key, out _);
            tcs.TrySetCanceled(cancellationToken);
        }
    }

    public async Task<Response> Send(Request request, CancellationToken cancellationToken)
    {
        var tcs = new TaskCompletionSource<Response>();

        try
        {
            // Добавьте запрос в словарь ожидающих запросов
            _pendingRequests.TryAdd(request.Id, tcs);

            // Отправьте запрос
            await _networkAdapter.Post(request, cancellationToken);

            // Дождитесь ответа
            var responseTask = await Task.WhenAny(tcs.Task, Task.Delay(_requestTimeout, cancellationToken));
            if (responseTask != tcs.Task)
            {
                // Время ожидания запроса истекло, поэтому удалите его из словаря
                _pendingRequests.TryRemove(request.Id, out _);

                // Отмените задачу запроса
                tcs.TrySetCanceled(cancellationToken);
                cancellationToken.ThrowIfCancellationRequested();
            }

            // Верните ответ
            return await tcs.Task;
        }
        catch (Exception ex)
        {
            // Удалите запрос из словаря, если произошла ошибка
            _pendingRequests.TryRemove(request.Id, out _);

            // Отмените задачу запроса
            tcs.TrySetException(ex);
            throw;
        }
    }
}
