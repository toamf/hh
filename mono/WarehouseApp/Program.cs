using System;
using System.Collections.Generic;
using WarehouseApp.Models;

namespace WarehouseApp
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Warehouse Application");

            // User input for boxes
            var boxes = GetUserInputForBoxes();

            // User input for pallets
            var pallets = GetUserInputForPallets();

            // Add boxes to pallets
            AddBoxesToPallets(boxes, pallets);

            // Display results
            DisplayResults(pallets);
        }

        static List<Box> GetUserInputForBoxes()
        {
            var boxes = new List<Box>();

            Console.Write("Enter the number of boxes: ");
            int boxCount = GetPositiveIntegerFromUser();

            for (int i = 0; i < boxCount; i++)
            {
                Console.WriteLine($"\nEnter details for Box {i + 1}:");

                double width = GetUserInputForDimension("Width");
                double height = GetUserInputForDimension("Height");
                double depth = GetUserInputForDimension("Depth");
                double weight = GetUserInputForDimension("Weight");

                Console.Write("Enter production date (dd.MM.yyyy) or leave empty to skip: ");
                DateTime? productionDate = GetDateFromUser();

                var box = new Box(width, height, depth, weight, productionDate);
                boxes.Add(box);
            }

            return boxes;
        }

        static List<Pallet> GetUserInputForPallets()
        {
            var pallets = new List<Pallet>();

            Console.Write("\nEnter the number of pallets: ");
            int palletCount = GetPositiveIntegerFromUser();

            for (int i = 0; i < palletCount; i++)
            {
                Console.WriteLine($"\nEnter details for Pallet {i + 1}:");

                double width = GetUserInputForDimension("Width");
                double height = GetUserInputForDimension("Height");
                double depth = GetUserInputForDimension("Depth");

                var pallet = new Pallet(width, height, depth);
                pallets.Add(pallet);
            }

            return pallets;
        }

        static void AddBoxesToPallets(List<Box> boxes, List<Pallet> pallets)
        {
            Console.WriteLine("\nAssign boxes to pallets:");
            foreach (var box in boxes)
            {
                Console.WriteLine($"Box {box.ID}, Volume: {box.CalculateVolume()} cubic meters, Expiry Date: {box.ExpiryDate?.ToShortDateString() ?? "N/A"}");
                Console.WriteLine("Available Pallets:");
                for (int i = 0; i < pallets.Count; i++)
                {
                    Console.WriteLine($"{i + 1}. Pallet {pallets[i].ID}, Volume: {pallets[i].CalculateVolume()} cubic meters");
                }

                while (true)
                {
                    Console.Write("Enter the number of the pallet to assign the box to (or leave empty to skip): ");
                    if (int.TryParse(Console.ReadLine(), out int palletNumber) && palletNumber > 0 && palletNumber <= pallets.Count)
                    {
                        var selectedPallet = pallets[palletNumber - 1];
                        if (box.Width <= selectedPallet.Width && box.Depth <= selectedPallet.Depth)
                        {
                            selectedPallet.AddBox(box);
                            break;
                        }
                        else
                        {
                            Console.WriteLine("The box dimensions exceed the pallet dimensions. Choose another pallet.");
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        static double GetUserInputForDimension(string dimensionName)
        {
            while (true)
            {
                Console.Write($"Enter {dimensionName} (in meters): ");
                if (double.TryParse(Console.ReadLine(), out double dimensionValue) && dimensionValue > 0)
                {
                    return dimensionValue;
                }
                else
                {
                    Console.WriteLine("Invalid input. Please enter a positive number.");
                }
            }
        }

        static int GetPositiveIntegerFromUser()
        {
            while (true)
            {
                if (int.TryParse(Console.ReadLine(), out int value) && value > 0)
                {
                    return value;
                }
                else
                {
                    Console.WriteLine("Invalid input. Please enter a positive integer.");
                }
            }
        }

        static DateTime? GetDateFromUser()
        {
            while (true)
            {
                string input = Console.ReadLine() ?? string.Empty;
                if (string.IsNullOrEmpty(input))
                {
                    return null;
                }
                if (DateTime.TryParse(input, out DateTime date) && date <= DateTime.Now)
                {
                    return date;
                }
                else
                {
                    Console.WriteLine("Invalid date. Ensure the format is dd.MM.yyyy and the date is not in the future.");
                }
            }
        }

        static void DisplayResults(List<Pallet> pallets)
        {
            // Display pallets grouped by expiry date and sorted by weight
            Console.WriteLine("\nPallets grouped by expiry date and sorted by weight:");
            var groupedPallets = pallets.GroupBy(p => p.CalculateExpiryDate()).OrderBy(g => g.Key);

            foreach (var group in groupedPallets)
            {
                Console.WriteLine($"Expiry Date: {group.Key?.ToShortDateString() ?? "N/A"}");
                foreach (var pallet in group.OrderBy(p => p.CalculateWeight()))
                {
                    Console.WriteLine($"\tPallet ID: {pallet.ID}, Weight: {pallet.CalculateWeight()}kg");
                }
            }

            Console.WriteLine("\nTop 3 pallets with boxes having the longest expiry date, sorted by volume:");
            var topPallets = pallets.OrderByDescending(p => p.CalculateExpiryDate()).ThenBy(p => p.CalculateVolume()).Take(3);

            foreach (var pallet in topPallets)
            {
                Console.WriteLine($"Pallet ID: {pallet.ID}, Expiry Date: {pallet.CalculateExpiryDate()?.ToShortDateString() ?? "N/A"}, Volume: {pallet.CalculateVolume()} cubic meters");
            }
        }
    }
}
