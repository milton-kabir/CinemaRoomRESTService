# CinemaRoomRESTService

This project can show the available seats, sell and refund tickets, and display the statistics of the venue.
I have used Spring boot and write the REST service.

## Examples
```yaml

Example 1: a GET /seats request

Response body:

{
   "total_rows":9,
   "total_columns":9,
   "available_seats":[
      {
         "row":1,
         "column":1,
         "price":10
      },
      {
         "row":1,
         "column":2,
         "price":10
      },
      {
         "row":1,
         "column":3,
         "price":10
      },

      ........

      {
         "row":9,
         "column":8,
         "price":8
      },
      {
         "row":9,
         "column":9,
         "price":8
      }
   ]
}


---------------------------------------------------------------------


Example 2: a correct POST /purchase request

Request body:

{
    "row": 3,
    "column": 4
}

Response body:

{
    "token": "e739267a-7031-4eed-a49c-65d8ac11f556",
    "ticket": {
        "row": 3,
        "column": 4,
        "price": 10
    }
}


-----------------------------------------------------------------------------------


Example 3: POST /return with the correct token

Request body:

{
    "token": "e739267a-7031-4eed-a49c-65d8ac11f556"
}

Response body:

{
    "returned_ticket": {
        "row": 1,
        "column": 2,
        "price": 10
    }
}


----------------------------------------------------------------------------------


Example 4: POST /return with an expired token

Request body:

{
    "token": "e739267a-7031-4eed-a49c-65d8ac11f556"
}

Response body:

{
    "error": "Wrong token!"
}


----------------------------------------------------------------------------------


Example 5: a POST /stats request with no parameters

Response body:

{
    "error": "The password is wrong!"
}


----------------------------------------------------------------------------------


Example 6: a POST /stats request with the correct password

Response body:

{
    "current_income": 30,
    "number_of_available_seats": 78,
    "number_of_purchased_tickets": 3
}
