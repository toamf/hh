import tkinter as tk
from tkinter import messagebox
from tkinter import simpledialog
import json
import os


class RetroCollector:
    def __init__(self):
        self.collection = []

    def add_item(self, title, item_type, date_added, date_manufacture, description):
        item = {
            'Title': title,
            'Type': item_type,
            'Date Added': date_added,
            'Date of Manufacture': date_manufacture,
            'Description': description
        }
        self.collection.append(item)

    def show_items_by_type(self, item_type):
        items = []
        for item in self.collection:
            if item['Type'] == item_type:
                items.append(item)
        return items

    def delete_item(self, item_index):
        if 0 <= item_index < len(self.collection):
            deleted_item = self.collection.pop(item_index)
            return deleted_item
        else:
            return None

    def edit_item(self, item_index, new_title, new_date_added, new_date_manufacture, new_description):
        if 0 <= item_index < len(self.collection):
            item = self.collection[item_index]
            item['Title'] = new_title
            item['Date Added'] = new_date_added
            item['Date of Manufacture'] = new_date_manufacture
            item['Description'] = new_description
            return True
        else:
            return False

def save_to_file(collector):
    with open("collection.json", "w") as file:
        json.dump(collector.collection, file)

def load_from_file(collector):
    if os.path.exists("collection.json"):
        with open("collection.json", "r") as file:
            collector.collection = json.load(file)

class RetroCollectorGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Python Collector")

        self.collector = RetroCollector()
        load_from_file(self.collector)

        self.menu_frame = tk.Frame(self.root)
        self.menu_frame.pack()

        self.menu_label = tk.Label(self.menu_frame, text="Menu:")
        self.menu_label.pack()

        self.add_button = tk.Button(self.menu_frame, text="Add Item to Collection", command=self.add_item)
        self.add_button.pack()

        self.show_button = tk.Button(self.menu_frame, text="Show Items in Collection", command=self.show_items)
        self.show_button.pack()

        self.delete_button = tk.Button(self.menu_frame, text="Delete Items from Collection", command=self.delete_items)
        self.delete_button.pack()

        self.save_button = tk.Button(self.menu_frame, text="Save to File", command=self.save_to_file)
        self.save_button.pack()

        self.load_button = tk.Button(self.menu_frame, text="Load from File", command=self.load_from_file)
        self.load_button.pack()

        self.exit_button = tk.Button(self.menu_frame, text="Exit", command=root.quit)
        self.exit_button.pack()

    def add_item(self):
        title = simpledialog.askstring("Add Item", "Title:")
        if title:
            types = ["Computer", "Camera", "Phone", "Video Player"]
            item_type = simpledialog.askinteger("Add Item", "Type:\n1. Computer\n2. Camera\n3. Phone\n4. Video Player",
                                                minvalue=1, maxvalue=4)
            if item_type:
                date_added = simpledialog.askstring("Add Item", "Date Added (dd/mm/yyyy):")
                if date_added:
                    date_manufacture = simpledialog.askstring("Add Item", "Date of Manufacture (dd/mm/yyyy):")
                    if date_manufacture:
                        description = simpledialog.askstring("Add Item", "Description:")
                        self.collector.add_item(title, types[item_type - 1], date_added, date_manufacture, description)
                        messagebox.showinfo("Add Item", "Item Added!")

    def show_items(self):
        types = ["Computer", "Camera", "Phone", "Video Player"]
        item_type = simpledialog.askinteger("Show Items", "Type:\n1. Computer\n2. Camera\n3. Phone\n4. Video Player",
                                            minvalue=1, maxvalue=4)
        if item_type:
            items = self.collector.show_items_by_type(types[item_type - 1])
            if items:
                items_text = "\n".join([f"{item['Title']}\t{item['Date Added']}\t{item['Date of Manufacture']}" for item in items])
                messagebox.showinfo("Show Items", f"Items:\nItem\tDate Added\tDate of Manufacture\n{items_text}")
            else:
                messagebox.showinfo("Show Items", "No items of this type found.")

    def delete_items(self):
        types = ["Computer", "Camera", "Phone", "Video Player"]
        item_type = simpledialog.askinteger("Delete Items", "Type:\n1. Computer\n2. Camera\n3. Phone\n4. Video Player",
                                            minvalue=1, maxvalue=4)
        if item_type:
            items = self.collector.show_items_by_type(types[item_type - 1])
            if items:
                item_titles = [item['Title'] for item in items]
                item_index = simpledialog.askinteger("Delete Items", f"Select the item to delete:\n{item_titles}",
                                                     minvalue=1, maxvalue=len(items))
                if item_index:
                    deleted_item = self.collector.delete_item(item_index - 1)
                    if deleted_item:
                        messagebox.showinfo("Delete Item", f"{deleted_item['Title']} Deleted!")
            else:
                messagebox.showinfo("Delete Items", "No items of this type found.")

    def save_to_file(self):
        save_to_file(self.collector)
        messagebox.showinfo("Save to File", "Data saved to file.")

    def load_from_file(self):
        load_from_file(self.collector)
        messagebox.showinfo("Load from File", "Data loaded from file.")

if __name__ == "__main__":
    root = tk.Tk()
    app = RetroCollectorGUI(root)
    root.mainloop()
