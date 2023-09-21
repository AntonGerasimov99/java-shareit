package ru.practicum.shareit.booking.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.booking.StatusEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "date_start", nullable = false)
    private LocalDateTime start;
    @Column(name = "date_end", nullable = false)
    private LocalDateTime end;
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer itemId;
    @JoinColumn(name = "booker_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer booker;
    @Column(name = "status")
    private StatusEnum status;
}
