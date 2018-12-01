#Wiley Bui buixx206
#I understand this is a graded, individual examination that may not be
#discussed with anyone. I also understand that obtaining solutions or
#partial solutions from outside sources or discussing any aspect of the exam
#with anyone is academic misconduct and will result in failing the course.
#I further certify that this program represents my own work and that none of
#it was obtained from any source other than material presented as part of the
#course
import turtle, math, random

def update_new_dot(grid, radius, origin, row, col):  #Updates a correct column & row in grid, and Displays x & y in turtle
    grid[row][col] = True

    turtle.hideturtle()
    turtle.up()
    turtle.goto(row, col)
    turtle.down()
    turtle.dot(5, (0.2, col/200.0, row/200.0))  #Beautify em dots
    return grid, radius, origin

def get_new_dot(grid, radius, origin): #Selects the correct dot to be placed
    found_dot = False
    while found_dot == False:
        theta = 2 * math.pi * random.random()   #Circle has 2 pi
        row = math.floor(radius * math.cos(theta)) + origin
        col = math.floor(radius * math.sin(theta)) + origin

        walked = random_walk(grid, row, col)
        if type(walked) == type([]):
            found_dot = True
            if abs(origin-walked[0]) >= radius or abs(origin-walked[1]) >= radius:  #Checks to see if current radius is greater than previous radius
                radius += 1
            elif math.sqrt((walked[0]-origin)**2 + (walked[1]-origin)**2) >= radius: #Maximize radius by using distance formula
                radius += 1
            return grid, radius, origin, walked[0], walked[1]

def random_walk(grid, row, col): #Repeatedly picks a random coordinate until it can stick to the tree
    two_hundred = 0
    located = False
    while two_hundred < 200 and located == False:
        two_hundred += 1
        check = hasNeighbor(grid, row, col)
        if check == True:
            located = True
            return [row, col]
        else:
            NSEW = random.randint(1,4)  #If doesn't stick, then randomly change 1 move
            if NSEW == 1:
                row += 1
            elif NSEW == 2:
                row -= 1
            elif NSEW == 3:
                col += 1
            else:
                col -= 1
    return None #200 random walks but still doesn't stick to tree

def hasNeighbor(grid, row, col):    #Checks around coordinates for placed neighbors
    if row < 0 or row > len(grid)-1 or col < 0 or col > len(grid)-1:    #Filter out current row/column if outside of grid
        return False
    else:
        for neighbor_row, neighbor_col in [
                        [row-1, col+1], [row, col+1], [row+1, col+1],
                        [row-1, col],                 [row+1, col],
                        [row-1, col-1], [row, col-1], [row+1, col-1]
                    ]:
            if neighbor_row > -1 and neighbor_col > -1 and neighbor_row < len(grid) and neighbor_col < len(grid): #Filter out if outside of grid
                if grid[neighbor_row][neighbor_col] == True:
                    return True
        return False

def main():
    turtle.speed(0)
    turtle.delay(0)
    turtle.setworldcoordinates(0, 0, 199, 199)
    screen = turtle.getscreen()
    user_input = screen.textinput("", "Enter tree size:")

    grid = []
    for my_row in range(200):
        my_column = []
        for i in range(200):
            my_column.append(False)
        grid.append(my_column)

    radius, origin, row, col = 0, 100, 100, 100
    for i in range(int(user_input)):    #Loops X times by user_input
        grid, radius, origin = update_new_dot(grid, radius, origin, row, col)
        grid, radius, origin, row, col = get_new_dot(grid, radius, origin)

if __name__ == '__main__':
    main()
