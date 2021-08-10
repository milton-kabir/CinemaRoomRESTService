package com.kabir.milton.CinemaRoomRESTService;

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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getPrice() {
        return row <= 4 ? 10 : 8;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}

class SeatsInfo {
    private final int totalRows;
    private final int totalColumns;
    private final boolean[][] seats;

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

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public List<Seat> getAvailableSeats() {
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
        int row = seat.getRow();
        int column = seat.getColumn();
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
        seatsInfo.returnSeat(seat.getRow(), seat.getColumn());
        return Map.of("returned_ticket", seat);
    }

    @PostMapping("/stats")
    public Object postStats(@RequestParam(required = false) String password) {
        final String correctPassword = "super_secret";
        if (!correctPassword.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "The password is wrong!"));
        }
        int income = 0;
        int sold = 0;
        for (Seat seat : purchases.values()) {
            income += seat.getPrice();
            sold++;
        }
        return Map.of("current_income", income,
                "number_of_available_seats", seatsInfo.calcTotalSeats() - sold,
                "number_of_purchased_tickets", sold);
    }
}
