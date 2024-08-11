import tkinter as tk
import random

class SnakeGame(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Snake Game")  # Sets the window title.
        self.geometry("800x800")  # Sets the window size to 800x800 pixels.
        self.resizable(False, False)  # Disables resizing of the window.

        # Game settings
        self.width = 800  
        self.height = 800  
        self.cell_size = 20  
        self.snake_direction = 'Right'  # Initial direction of the snake.
        self.snake = [(100, 100), (80, 100), (60, 100)]  # Initial segments of the snake.
        self.foods = []  # List to store food.
        self.foods = self.place_food(20)  # Places initial foods.
        self.score = 0 

        # UI setup
        self.menu_frame = tk.Frame(self)  # Frame for holding game UI elements like score.
        self.menu_frame.pack(fill="both", expand=True)

        self.game_frame = tk.Frame(self)  # Frame for the game canvas.
        self.canvas = tk.Canvas(self.game_frame, bg='black', width=self.width, height=self.height)  # Game canvas.
        self.score_label = tk.Label(self.menu_frame, text=f"Score: {self.score}", font=('Arial', 24))  # Score display.
        self.score_label.pack(side="top", fill="x")
        self.start_game()

    def start_game(self):
        self.game_frame.pack(fill="both", expand=True)
        self.canvas.pack()
        self.setup_ui()
        self.run_game()

    def setup_ui(self):
        self.update_ui()
        self.bind("<KeyPress>", self.change_direction)  

    def update_ui(self):
        self.canvas.delete('all')  # Clears the canvas.
        # Draws the snake.
        for x, y in self.snake:
            self.canvas.create_rectangle(x, y, x + self.cell_size, y + self.cell_size, fill='green')
        # Draws the food.
        for x, y in self.foods:
            self.canvas.create_oval(x, y, x + self.cell_size, y + self.cell_size, fill='blue')

    def run_game(self):
        self.move_snake()
        self.after(100, self.run_game)  # Calls itself every 100 milliseconds.

    def move_snake(self):
        head_x, head_y = self.snake[0]  # Current head position.
        # Update head position based.
        if self.snake_direction == 'Left':
            head_x -= self.cell_size
        elif self.snake_direction == 'Right':
            head_x += self.cell_size
        elif self.snake_direction == 'Up':
            head_y -= self.cell_size
        elif self.snake_direction == 'Down':
            head_y += self.cell_size

        new_head = (head_x, head_y) 

        # Check for collisions with walls or itself.
        if head_x < 0 or head_x >= self.width or head_y < 0 or head_y >= self.height - 40: #40 is for the label(I have computed it).
            self.gameover()
            return

        if new_head in self.snake:
            self.gameover()
            return
      
        # Eat food and grow.
        if new_head in self.foods:
            self.snake = [new_head] + self.snake  # Add new head and keep tail.
            self.foods.remove(new_head)  # Remove eaten food.
            self.foods.append(self.place_food(1)[0])  # Add new food.
            self.score += 1  # Increment score.
            self.score_label.config(text=f"Score: {self.score}")
        else:
            self.snake = [new_head] + self.snake[:-1]  

        self.update_ui()

    def change_direction(self, event):
        # Map to check opposite directions to prevent reversing.
        opposite_directions = {'Left': 'Right', 'Right': 'Left', 'Up': 'Down', 'Down': 'Up'}
        if event.keysym in ['Left', 'Right', 'Up', 'Down']:
            if event.keysym != opposite_directions[self.snake_direction]:
                self.snake_direction = event.keysym  # Change direction if not opposite.
    
    def place_food(self, count):
        temp_foods = []
        while len(temp_foods) < count:
            x = random.randint(0, (self.width - self.cell_size) // self.cell_size) * self.cell_size
            y = random.randint(0, (self.height - 40 - self.cell_size) // self.cell_size) * self.cell_size #40 is for the label.
            if (x, y) not in self.snake and (x, y) not in temp_foods and (x, y) not in self.foods: #In case the position of new food is the same as the old foods.
                temp_foods.append((x, y))  # Append new food if not overlapping.

        return temp_foods
    
    def gameover(self):
        self.score_label.config(text=f"Score: {self.score} Game Over!")  # Display game over message.
        self.unbind("<KeyPress>")  # Stop responding to key presses.

if __name__ == "__main__":
    game = SnakeGame()
    game.mainloop()  
