using System;
using System.Collections.Generic;
using System.Linq;

public class Box
{
    public Box(decimal width, decimal height, decimal depth, decimal weight, DateTime? manufactureDate)
    {
        if (width <= 0 || height <= 0 || depth <= 0 || weight <= 0)
            throw new ArgumentOutOfRangeException("Dimensions and weight must be positive.");

        ID = Guid.NewGuid();
        Width = width;
        Height = height;
        Depth = depth;
        Weight = weight;
        ManufactureDate = manufactureDate;
    }

    public Guid ID { get; }
    public decimal Width { get; }
    public decimal Height { get; }
    public decimal Depth { get; }
    public decimal Weight { get; }
    public DateTime? ManufactureDate { get; }
    public DateTime ExpiryDate => ManufactureDate.HasValue ? ManufactureDate.Value.AddDays(100) : DateTime.MaxValue;
    public decimal Volume => Width * Height * Depth;
}

public class Pallet
{
    private List<Box> boxes = new List<Box>();

    public Pallet(decimal width, decimal height, decimal depth)
    {
        if (width <= 0 || height <= 0 || depth <= 0)
            throw new ArgumentOutOfRangeException("Dimensions must be positive.");

        ID = Guid.NewGuid();
        Width = width;
        Height = height;
        Depth = depth;
    }

    public Guid ID { get; }
    public decimal Width { get; }
    public decimal Height { get; }
    public decimal Depth { get; }
    public IReadOnlyCollection<Box> Boxes => boxes.AsReadOnly();
    public DateTime ExpiryDate => Boxes.Any() ? Boxes.Min(b => b.ExpiryDate) : DateTime.MaxValue;
    public decimal Weight => Boxes.Sum(b => b.Weight) + 30;
    public decimal Volume => Boxes.Sum(b => b.Volume) + Width * Height * Depth;

    public void AddBox(Box box)
    {
        if (box == null) throw new ArgumentNullException(nameof(box));
        if (box.Width > Width || box.Depth > Depth)
            throw new Exception("Box dimensions exceed pallet dimensions.");

        boxes.Add(box);
    }
}

public class Warehouse
{
    private List<Pallet> pallets = new List<Pallet>();

    public IReadOnlyCollection<Pallet> Pallets => pallets.AsReadOnly();

    public void AddPallet(Pallet pallet)
    {
        if (pallet == null) throw new ArgumentNullException(nameof(pallet));
        pallets.Add(pallet);
    }
}

public class WarehouseConsoleApp
{
    private readonly Warehouse warehouse;

    public WarehouseConsoleApp()
    {
        warehouse = new Warehouse();
    }

    public void Run()
    {
        while (true)
        {
            Console.WriteLine("\n1. Add Pallet");
            Console.WriteLine("2. Add Box to a Pallet");
            Console.WriteLine("3. Display Pallets Grouped By Expiry Date");
            Console.WriteLine("4. Display Top 3 Pallets By Expiry Date");
            Console.WriteLine("5. Exit");
            
            switch (Console.ReadLine())
            {
                case "1":
                    AddPallet();
                    break;
                case "2":
                    AddBox();
                    break;
                case "3":
                    PrintPalletsGroupedByExpiryDate();
                    break;
                case "4":
                    PrintTop3PalletsByExpiryDate();
                    break;
                case "5":
                    Environment.Exit(0);
                    break;
                default:
                    Console.WriteLine("\nInvalid option. Please enter a number between 1 and 5.");
                    break;
            }
        }
    }

    private void AddPallet()
    {
        Console.WriteLine("\nEnter Pallet Width, Height and Depth:");
        decimal width, height, depth;
        if (decimal.TryParse(Console.ReadLine(), out width) && decimal.TryParse(Console.ReadLine(), out height) && decimal.TryParse(Console.ReadLine(), out depth))
        {
            var pallet = new Pallet(width, height, depth);
            warehouse.AddPallet(pallet);
            Console.WriteLine($"Pallet {pallet.ID} added.");
        }
        else
        {
            Console.WriteLine("Invalid input. Please try again.");
        }
    }

    private void AddBox()
    {
        Console.WriteLine("\nEnter Pallet ID:");
        Guid palletId;
        if (Guid.TryParse(Console.ReadLine(), out palletId))
        {
            var pallet = warehouse.Pallets.FirstOrDefault(p => p.ID == palletId);
            if (pallet != null)
            {
                Console.WriteLine("\nEnter Box Width, Height, Depth, Weight and Manufacture Date (yyyy-MM-dd):");
                decimal width, height, depth, weight;
                DateTime manufactureDate;
                if (decimal.TryParse(Console.ReadLine(), out width) && decimal.TryParse(Console.ReadLine(), out height) && decimal.TryParse(Console.ReadLine(), out depth) && decimal.TryParse(Console.ReadLine(), out weight) && DateTime.TryParseExact(Console.ReadLine(), "yyyy-MM-dd", null, System.Globalization.DateTimeStyles.None, out manufactureDate))
                {
                    var box = new Box(width, height, depth, weight, manufactureDate);
                    pallet.AddBox(box);
                    Console.WriteLine($"Box {box.ID} added to Pallet {palletId}.");
                }
                else
                {
                    Console.WriteLine("Invalid input. Please try again.");
                }
            }
            else
            {
                Console.WriteLine("Pallet not found.");
            }
        }
        else
        {
            Console.WriteLine("Invalid Pallet ID. Please try again.");
        }
    }

    private void PrintPalletsGroupedByExpiryDate()
    {
        var grouped = warehouse.Pallets
            .GroupBy(p => p.ExpiryDate)
            .OrderBy(g => g.Key);

        foreach (var group in grouped)
        {
            Console.WriteLine($"Expiry date: {group.Key.ToShortDateString()}");
            foreach (var pallet in group.OrderBy(p => p.Weight))
            {
                Console.WriteLine($"Pallet ID: {pallet.ID}, Weight: {pallet.Weight}, Volume: {pallet.Volume}");
            }
        }
    }

    private void PrintTop3PalletsByExpiryDate()
    {
        var top3 = warehouse.Pallets
            .OrderByDescending(p => p.ExpiryDate)
            .ThenBy(p => p.Volume)
            .Take(3);

        foreach (var pallet in top3)
        {
            Console.WriteLine($"Pallet ID: {pallet.ID}, Weight: {pallet.Weight}, Volume: {pallet.Volume}, Expiry Date: {pallet.ExpiryDate.ToShortDateString()}");
        }
    }
}

public class Program
{
    public static void Main()
    {
        var app = new WarehouseConsoleApp();
        app.Run();
    }
}
