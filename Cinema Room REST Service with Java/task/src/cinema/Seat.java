package cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Seat {

    private int row;
    private int column;
    private int price;
    @JsonIgnore
    private boolean empty;

    public Seat(){}

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;

        if (row <= 4) {
            this.price = 10;
        } else {
            this.price = 8;
        }
        this.empty = true;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void reserve() {
        this.empty = false;
    }

    public void clearSeat() {
        this.empty = true;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}