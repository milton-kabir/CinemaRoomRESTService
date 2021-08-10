package com.kabir.milton.CinemaRoomRESTService;

import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
public class CinemaRoomRestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaRoomRestServiceApplication.class, args);
    }

}

class Seat {
    private int row;
    private int column;

    public Seat() {
    }

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getrow() {
        return row;
    }

    public int getcolumn() {
        return column;
    }

    public int getprice() {
        return row <= 4 ? 10 : 8;
    }

    public void setrow(int row) {
        this.row = row;
    }

    public void setcolumn(int column) {
        this.column = column;
    }
}

class SeatsInfo {
    private final int totalRows;
    private final int totalColumns;
    private final boolean[][] seats; // true if available

    public SeatsInfo() {
        totalRows = 9;
        totalColumns = 9;
        seats = new boolean[totalRows][];
        for (int i = 0; i < totalRows; i++) {
            seats[i] = new boolean[totalColumns];
            for (int j = 0; j < totalColumns; j++) {
                seats[i][j] = true;
            }
        }
    }

    public int gettotal_rows() {
        return totalRows;
    }

    public int gettotal_columns() {
        return totalColumns;
    }

    public List<Seat> getavailable_seats() {
        List<Seat> result = new ArrayList<>();
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (seats[i][j]) result.add(new Seat(i + 1, j + 1));
            }
        }
        return result;
    }

    public int calcTotalSeats() {
        return totalRows * totalColumns;
    }

    public boolean isInBounds(int row, int column) {
        return row >= 0 && row < totalRows && column >= 0 && column < totalColumns;
    }

    public boolean isAvailable(int row, int column) {
        return seats[row][column];
    }

    public void purchaseSeat(int row, int column) {
        seats[row][column] = false;
    }

    public void returnSeat(int row, int column) {
        seats[row][column] = true;
    }
}


@RestController
class SeatsController {
    private final HashMap<String, Seat> purchases = new HashMap<>();
    private final SeatsInfo seatsInfo;

    public SeatsController() {
        seatsInfo = new SeatsInfo();
    }

    @GetMapping("/seats")
    public SeatsInfo GetSeatsInfo() {
        return seatsInfo;
    }

    @PostMapping("/purchase")
    public Object postPurchase(@RequestBody Seat seat) {
        int row = seat.getrow();
        int column = seat.getcolumn();

        if (!seatsInfo.isInBounds(row, column)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "The number of a row or a column is out of bounds!"));
        }

        if (!seatsInfo.isAvailable(row, column)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "The ticket has been already purchased!"));
        }

        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        purchases.put(token, seat);
        seatsInfo.purchaseSeat(row, column);
        return Map.of("token", token, "ticket", seat);
    }

    @PostMapping("/return")
    public Object postReturn(@RequestBody Map<String, String> map) {
        String token = map.get("token");
        Seat seat = purchases.remove(token);
        if (seat == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Wrong token!"));
        }

        seatsInfo.returnSeat(seat.getrow(), seat.getcolumn());
        return Map.of("returned_ticket", seat);
    }
}
