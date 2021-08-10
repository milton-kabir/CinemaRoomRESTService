package com.kabir.milton.CinemaRoomRESTService;

import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@SpringBootApplication
public class CinemaRoomRestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaRoomRestServiceApplication.class, args);
    }

}
class Seats {

    private int total_rows;
    private int total_columns;
    private List<AvailableSeats> available_seats;

    public Seats(int total_rows, int total_columns, List<AvailableSeats> available_seats) {
        this.total_rows = total_rows;
        this.total_columns = total_columns;
        this.available_seats = available_seats;
    }

    public Seats() {
    }

    public int getTotal_rows() {
        return total_rows;
    }

    public void setTotal_rows(int total_rows) {
        this.total_rows = total_rows;
    }

    public int getTotal_columns() {
        return total_columns;
    }

    public void setTotal_columns(int total_columns) {
        this.total_columns = total_columns;
    }

    public List<AvailableSeats> getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(List<AvailableSeats> available_seats) {
        this.available_seats = available_seats;
    }
}

class AvailableSeats {

    private int row;
    private int column;
    private int price;

    public AvailableSeats() {
    }

    public AvailableSeats(int row, int column, int price) {
        this.row = row;
        this.column = column;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
}

class Purchase {

    private int row;
    private int column;
    private int price;
    //private String message;


    @Override
    public String toString() {
        return "{" +
                "\"row\":" + row +
                ", \"column\":" + column +
                ", \"price\":" + price +
                '}';
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Purchase(int row, int column, int price) {
        this.row = row;
        this.column = column;
        this.price = price;
    }

    public Purchase() {
    }
}

class SeatsExceptions {

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public SeatsExceptions() {
    }

    public SeatsExceptions(String error) {
        super();
        this.error = error;
    }
}


@ControllerAdvice
@RestController
class CustomizedResponseEntityExceptionHandler {

    @ExceptionHandler(SeatNotFoundException.class)
    public final ResponseEntity<SeatsExceptions> handleNotFoundException(SeatNotFoundException ex, WebRequest request) {
        SeatsExceptions exceptionResponse = new SeatsExceptions(ex.getMessage());
        return new ResponseEntity<SeatsExceptions>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}

@RestController
class AvailableSeatsController {
    public static List<AvailableSeats> list = new ArrayList<>();

    static {
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                list.add(new AvailableSeats(i, j, i <= 4 ? 10 : 8));
            }
        }
    }

    @GetMapping("/seats")
    public Seats availableSeats() {
        Seats capacity = new Seats();
        capacity.setTotal_rows(9);
        capacity.setTotal_columns(9);
        capacity.setAvailable_seats(list);
        return capacity;
    }

    @PostMapping(value = "/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    public Purchase purchaseTicket(@RequestBody AvailableSeats availableSeats, HttpServletResponse response) {
        if (availableSeats.getRow() > 9 || availableSeats.getRow() < 0
                || availableSeats.getColumn() > 9 || availableSeats.getColumn() < 0) {
            throw new SeatNotFoundException("The number of a row or a column is out of bounds!");
        }
        Optional<AvailableSeats> seat = list.stream()
                .filter(x -> x.getRow() == availableSeats.getRow() && x.getColumn() == availableSeats.getColumn())
                .findAny();
        if (seat.isPresent()) {
            //sell ticket
            Purchase purchase = new Purchase(seat.get().getRow(), seat.get().getColumn(), seat.get().getPrice());
            list.remove(seat.get());
            return purchase;
        } else {
            throw new SeatNotFoundException("The ticket has been already purchased!");
        }
    }

}

@ResponseStatus(HttpStatus.NOT_FOUND)
class SeatNotFoundException extends RuntimeException{

    public SeatNotFoundException(String message) {
        super(message);
    }
}