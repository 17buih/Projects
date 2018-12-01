#Wiley Bui buixx206
#I understand this is a graded, individual examination that may not be
#discussed with anyone. I also understand that obtaining solutions or
#partial solutions from outside sources or discussing any aspect of the exam
#with anyone is academic misconduct and will result in failing the course.
#I further certify that this program represents my own work and that none of
#it was obtained from any source other than material presented as part of the
#course.
import turtle, random
class Cell:
    def __init__(self, Turtle, xmin, ymin, width, height):
        self.__xmin = int(xmin)
        self.__ymin = int(ymin)
        self.__xmax = int(xmin + width)
        self.__ymax = int(ymin + height)
        self.__t = Turtle
        self.__bomb = False
        self.__cleared = False
        self.__t.fillcolor('#1BF53A') #GREEN

    def isIn(self, x, y): #Check XY coordinates are in square grid
        return (x > self.__xmin and x < self.__xmax and y > self.__ymin and y < self.__ymax)

    def setBomb(self):
        self.__bomb = True

    def isBomb(self): #Check if has a bomb
        return self.__bomb

    def clear(self):
        self.__t.fillcolor('#FE291F') if self.isBomb() else self.__t.fillcolor('#CACACA') #RED if mine, other things: gray
        self.__cleared = True
        self.draw()

    def isCleared(self):
        return self.__cleared

    def showCount(self, c): #Shows either a number or an asterisk when there's a bomb in the center
        self.__t.hideturtle()
        self.__t.penup()
        self.__t.goto((self.__xmax+self.__xmin)/2, (self.__ymax+self.__ymin)/2-9)
        self.__t.pendown()
        self.__t.write(str(c)[0], align="center", font=("Arial", 12, "bold"))

    def draw(self): #Draw or redraw a square
        self.__t.hideturtle()
        self.__t.penup()
        self.__t.goto(self.__xmin, self.__ymin)
        self.__t.pendown()
        self.__t.begin_fill()
        for i in range(2):
            self.__t.forward(self.__xmax - self.__xmin)
            self.__t.left(90)
            self.__t.forward(self.__ymax - self.__ymin)
            self.__t.left(90)
        self.__t.end_fill()


class Minesweeper:
    def __init__(self, rows, columns, mines, all_bombs_visible = False):
        self.__grid = []
        self.__t = turtle.Turtle()
        self.__s = self.__t.getscreen()
        self.__t.speed(0)
        self.__s.delay(0)

        row_inbetween_space = 3 * (columns-1)  #Adds 3 pixels for each space in between
        col_inbetween_space = 3 * (rows-1)
        width = round((360-row_inbetween_space)/columns)   #Grid sets to 360x360 pixels, adding/removing rows&columns doesn't affect grid
        height = round((360-col_inbetween_space)/rows)

        y = 180
        for e in range(rows):  #Lists all rows and columns in the big grid
            x = -180
            my_column = []
            for i in range(columns):
                my_cell = Cell(self.__t, x, y, width, height)
                my_column.append(my_cell)
                my_cell.draw()
                x += (width + 3)
            self.__grid.append(my_column)
            y -= (height + 3)

        i = 0
        while i < mines and i < (rows * columns):  #prevents maximum mines if exceeds the total # of cells
            create_random_mine = [random.randint(0, rows-1), random.randint(0, columns-1)]
            if not self.__grid[create_random_mine[0]][create_random_mine[1]].isBomb():
                self.__grid[create_random_mine[0]][create_random_mine[1]].setBomb()
                i += 1

        if all_bombs_visible == True:
            self.displayAllBombs()
        self.__s.onclick(self.__mouseClick)  #When a user clicks on the canvas

    def getRowColSize(self):
        count_row = 0
        for row in self.__grid:
            count_row += 1
            count_col = 0
            for col in row:
                count_col += 1
        return count_row, count_col

    def displayAllBombs(self): #Displays all bombs
        row = -1
        for each_row in self.__grid:
            row += 1
            col = -1
            for my_col in each_row:
                col += 1
                if self.__grid[row][col].isBomb():
                    self.__grid[row][col].clear()
                    self.__grid[row][col].showCount('*')

    def countBombs(self, row, col): #Checking neighbors and count how many bombs total
        bombs_found = 0
        for neighbor_row, neighbor_col in [ [row-1, col+1], [row, col+1], [row+1, col+1],
                                            [row-1, col],                 [row+1, col],
                                            [row-1, col-1], [row, col-1], [row+1, col-1] ]:
            if neighbor_row > -1 and neighbor_col > -1 and neighbor_row < self.getRowColSize()[0] and neighbor_col < self.getRowColSize()[1]: #All points should be in grid
                if self.__grid[neighbor_row][neighbor_col].isBomb():
                    bombs_found += 1
        return bombs_found

    def cellsRemaining(self):  #Green cells that are left in grid; excludes bombs
        count, row = 0, -1
        for each_row in self.__grid:
            row += 1
            col = -1
            for my_col in each_row:
                col += 1
                if not self.__grid[row][col].isBomb() and not self.__grid[row][col].isCleared():
                    count += 1
        return count

    def getRowCol(self, x, y): #Get actual row&column from a mouse click XY coordinates
        row = -1
        for each_row in self.__grid:
            row += 1
            col = -1
            for each_cell in each_row:
                col += 1
                if self.__grid[row][col].isIn(x, y) == True:
                    return row, col
        return -1, -1

    def showedAllDisplayedMines(self):    #Checks if mines are already appeared on grid
        count, row = 0, -1
        for each_row in self.__grid:
            row += 1
            col = -1
            for each_col in each_row:
                col += 1
                if self.__grid[row][col].isCleared() and self.__grid[row][col].isBomb():
                    return True
        return False

    def __mouseClick(self, x, y): #User clicks a point on canvas
        if self.showedAllDisplayedMines() == False:
            row, col = self.getRowCol(x, y)
            if not (row == -1 and col == -1) and not self.__grid[row][col].isCleared():
                count = self.countBombs(row, col)
                self.__grid[row][col].clear()
                if count != 0 and not self.__grid[row][col].isBomb(): #Has a number in grid
                    self.__grid[row][col].showCount(count)
                    if self.cellsRemaining() == 0 and not self.showedAllDisplayedMines():  #Player wins when there are no green cells left
                        self.displayAllBombs()
                        self.endGameWrite('Congratulations! You Win!')
                elif self.__grid[row][col].isBomb():    #Game's over when clicks on a bomb
                    self.displayAllBombs()
                    self.endGameWrite('Game Over!')
                else:
                    self.clearCell(row, col)
        else:
            self.__s.exitonclick()

    def clearCell(self, row, col):  #Displays neighboring ghost cells that are either have no bombs or number
        for neighbor_row, neighbor_col in [ [row-1, col+1], [row, col+1], [row+1, col+1],
                                            [row-1, col],                 [row+1, col],
                                            [row-1, col-1], [row, col-1], [row+1, col-1] ]:
            if neighbor_row > -1 and neighbor_col > -1 and neighbor_row < self.getRowColSize()[0] and neighbor_col < self.getRowColSize()[1]:
                if not self.__grid[neighbor_row][neighbor_col].isBomb() and not self.__grid[neighbor_row][neighbor_col].isCleared():
                    count = self.countBombs(neighbor_row, neighbor_col)
                    if count == 0:
                        self.__grid[neighbor_row][neighbor_col].clear()
                        self.clearCell(neighbor_row, neighbor_col)  #Recursion - check neighbor's neighbor's neighbor's... ghosts cells
                    else:
                        if self.countBombs(row, col) == 0:
                            self.__grid[neighbor_row][neighbor_col].clear()
                            self.__grid[neighbor_row][neighbor_col].showCount(count)

        if self.cellsRemaining() == 0 and not self.showedAllDisplayedMines():  #Player wins when there are no green cells left
            self.displayAllBombs()
            self.endGameWrite('Congratulations! You Win!')

    def endGameWrite(self, msg):
        location = [-200, -220]
        fontMsg = [msg, '(click mouse to exit)']
        fontSize = [15, 11]
        fontType = ['bold', 'normal']
        for i in range(2):
            self.__t.hideturtle()
            self.__t.penup()
            self.__t.goto(0, location[i])
            self.__t.pendown()
            self.__t.write(fontMsg[i], align="center", font=("Arial", fontSize[i], fontType[i])) #Displays msg if winner win or lose

def main():
    Minesweeper(14, 14, 15)

if __name__ == "__main__":
    main()
