package com.kabir.milton.CinemaRoomRESTService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CinemaRoomRestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaRoomRestServiceApplication.class, args);
	}

}
class AvailableSeats {

	private int row;
	private int column;

	public AvailableSeats() {
	}

	public AvailableSeats(int row, int column) {
		this.row = row;
		this.column = column;
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

@RestController
class AvailableSeatsController {

	@GetMapping("/seats")
	public Seats availableSeats() {
		Seats capacity = new Seats();
		capacity.setTotal_rows(9);
		capacity.setTotal_columns(9);
		List<AvailableSeats> list = new ArrayList<>();
		for (int i = 1; i <= capacity.getTotal_rows(); i++) {
			for (int j = 1; j <= capacity.getTotal_columns(); j++) {
				list.add(new AvailableSeats(i, j));
			}
		}
		capacity.setAvailable_seats(list);
		return capacity;
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
