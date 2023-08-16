using System;

namespace WarehouseApp.Models
{
    public class Box
    {
        public Guid ID { get; private set; }
        public double Width { get; private set; }
        public double Height { get; private set; }
        public double Depth { get; private set; }
        public double Weight { get; private set; }
        public DateTime? ProductionDate { get; private set; }
        public DateTime? ExpiryDate { get; private set; }

        public Box(double width, double height, double depth, double weight, DateTime? productionDate = null, DateTime? expiryDate = null)
        {
            ID = Guid.NewGuid();
            Width = width;
            Height = height;
            Depth = depth;
            Weight = weight;
            ProductionDate = productionDate;
            ExpiryDate = expiryDate;

            if (ExpiryDate == null && ProductionDate != null)
            {
                ExpiryDate = ProductionDate.Value.AddDays(100);
            }
        }

        public double CalculateVolume()
        {
            return Width * Height * Depth;
        }
    }
}
