using System;
using System.Collections.Generic;
using System.Linq;

namespace WarehouseApp.Models
{
    public class Pallet
    {
        public Guid ID { get; private set; }
        public double Width { get; private set; }
        public double Height { get; private set; }
        public double Depth { get; private set; }
        private List<Box> _boxes;

        public Pallet(double width, double height, double depth)
        {
            ID = Guid.NewGuid();
            Width = width;
            Height = height;
            Depth = depth;
            _boxes = new List<Box>();
        }

        public void AddBox(Box box)
        {
            if (box.Width <= Width && box.Depth <= Depth)
            {
                _boxes.Add(box);
            }
            else
            {
                throw new ArgumentException("Box dimensions exceed pallet dimensions.");
            }
        }

        public DateTime? CalculateExpiryDate()
        {
            if (_boxes.Any())
            {
                return _boxes.Min(b => b.ExpiryDate);
            }
            return null;
        }

        public double CalculateWeight()
        {
            return _boxes.Sum(b => b.Weight) + 30;
        }

        public double CalculateVolume()
        {
            double totalVolume = _boxes.Sum(b => b.CalculateVolume());
            double palletVolume = Width * Height * Depth;
            return totalVolume + palletVolume;
        }
    }
}
