using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using WarehouseApp.Models;

namespace WarehouseApp.Tests
{
    [TestClass]
    public class WarehouseTests
    {
        [TestMethod]
        public void CreateBox_ValidValues_ShouldCreateBoxSuccessfully()
        {
            var box = new Box(10, 10, 10, 5, DateTime.Now);

            Assert.IsNotNull(box);
            Assert.AreEqual(10, box.Width);
            Assert.AreEqual(10, box.Height);
            Assert.AreEqual(10, box.Depth);
            Assert.AreEqual(5, box.Weight);
        }

        [TestMethod]
        public void AddBoxToPallet_ValidBox_ShouldNotThrowException()
        {
            var pallet = new Pallet(100, 100, 100);
            var box = new Box(10, 10, 10, 5, DateTime.Now);
            
            // Мы просто проверим, что не возникает исключения
            pallet.AddBox(box);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void AddBoxToPallet_InvalidBox_ShouldThrowException()
        {
            var pallet = new Pallet(10, 10, 10);
            var box = new Box(20, 20, 20, 5, DateTime.Now);

            pallet.AddBox(box);
        }

        [TestMethod]
        public void CalculateBoxVolume_ValidBox_ShouldReturnCorrectVolume()
        {
            var box = new Box(10, 10, 10, 5, DateTime.Now);
            
            double volume = box.CalculateVolume();

            Assert.AreEqual(1000, volume);
        }

        [TestMethod]
        public void CalculatePalletVolume_WithBoxes_ShouldReturnCorrectVolume()
        {
            var pallet = new Pallet(100, 100, 100);
            var box1 = new Box(10, 10, 10, 5, DateTime.Now);
            var box2 = new Box(20, 20, 20, 10, DateTime.Now.AddDays(-5));
            
            pallet.AddBox(box1);
            pallet.AddBox(box2);

            double volume = pallet.CalculateVolume();

            Assert.AreEqual(1000000 + 1000 + 8000, volume);
        }
    }
}
