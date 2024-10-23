package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController

public class Theatre {


    private final int rows = 9;
    private final int columns = 9;
    private List<Seat> seats;
    private Map<UUID, Seat> purchases;
    private int income;
    private int available;
    private int purchased;
    private String hashedPassword;

    public Theatre() {
        seats = Collections.synchronizedList(new ArrayList<>());
        populate();
        purchases = Collections.synchronizedMap(new HashMap<>());
        income = 0;
        available = rows * columns;
        purchased = 0;
        hashedPassword = BCrypt.hashpw("super_secret", BCrypt.gensalt());
    }

    @GetMapping("/seats")
    public Theatre seats() {
        return this;
    }

    @PostMapping("/purchase")
    public synchronized ResponseEntity<?> purchase(@RequestBody Seat aSeat) {
        int row = aSeat.getRow();
        int column = aSeat.getColumn();
        if (column > columns || row > rows || column < 1 || row < 1) {
            return new ResponseEntity<>(Map.of("error", "The number of a row or a column is out of bounds!"), HttpStatus.BAD_REQUEST);
        }
        Seat seat = seats.get(((row - 1) * columns) + (column - 1));

        if (seat.isEmpty()) {
            seat.reserve();
            UUID uuid = UUID.randomUUID();
            purchases.put(uuid, seat);
            income += seat.getPrice();
            available--;
            purchased++;
            return new ResponseEntity<>(Map.of("token", uuid.toString(), "ticket", seat), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("error", "The ticket has been already purchased!"), HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping("/return")
    public synchronized ResponseEntity<?> ticketReturn(@RequestBody Token token) {
        UUID uuid = token.getToken();
        if (purchases.containsKey(uuid)) {
            Seat seat = purchases.get(uuid);
            seat.clearSeat();
            purchases.remove(uuid);
            income -= seat.getPrice();
            available++;
            purchased--;
            return new ResponseEntity<>(Map.of("ticket", seat), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("error", "Wrong token!"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats(@RequestParam() Optional<String> password) {
        if (password.isPresent() && BCrypt.checkpw(password.get(), hashedPassword)) {
            return new ResponseEntity<>(Map.of("income", income, "available", available, "purchased", purchased), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("error", "The password is wrong!"), HttpStatus.UNAUTHORIZED);

    }

    public void populate() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                seats.add(new Seat(i + 1, j + 1));
            }
        }
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
