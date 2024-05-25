from flask import Flask, request
import threading
import requests
import webbrowser
import time


CLIENT_ID = 'ID'
CLIENT_SECRET = 'TOKEN'
REDIRECT_URI = 'http://127.0.0.1:5000/callback'
AUTH_URL = 'https://www.donationalerts.com/oauth/authorize'
TOKEN_URL = 'https://www.donationalerts.com/oauth/token'
app = Flask(__name__)


@app.route('/callback')
def callback():
    code = request.args.get('code')
    access_token = exchange_code_for_token(code)
    threading.Thread(target=listen_for_donations, args=(access_token,)).start()
    return 'Программа успешно запущена. Данную страницу можно закрыть.'


def get_authorization():
    # Запрашиваем код авторизации
    params = {
        'client_id': CLIENT_ID,
        'redirect_uri': REDIRECT_URI,
        'response_type': 'code',
        'scope': 'oauth-donation-index'
    }
    request_url = requests.Request('GET', AUTH_URL, params=params).prepare().url
    webbrowser.open(request_url)


def exchange_code_for_token(code):
    # Обмениваем код на токен доступа
    data = {
        'grant_type': 'authorization_code',
        'client_id': CLIENT_ID,
        'client_secret': CLIENT_SECRET,
        'code': code,
        'redirect_uri': REDIRECT_URI
    }
    response = requests.post(TOKEN_URL, data=data)
    return response.json()['access_token']


def listen_for_donations(access_token):
    url = "https://www.donationalerts.com/api/v1/alerts/donations"
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    last_id = 0  # Последний обработанный ID доната

    while True:
        try:
            response = requests.get(url, headers=headers, params={'after': last_id})
            donations = response.json()['data']
            if donations:
                for donation in donations:
                    if donation['id'] > last_id:
                        print("Получено пожертвование!")
                        last_id = donation['id']
        except Exception as e:
            print(f"Error: {e}")
        time.sleep(10)  # Время обновления 10 секунд


if __name__ == '__main__':
    get_authorization()
    app.run(port=5000)
