package com.sparta.interparty.domain.reservation.entity

import com.sparta.interparty.domain.reservation.enums.ReservationStatus
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.global.entity.TimeStamped
import jakarta.persistence.*
import org.springframework.boot.actuate.endpoint.Show
import java.util.*

@Entity
@Table(name = "reservation")
class Reservation  (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id : UUID,
    @ManyToOne
    @JoinColumn(name = "reserver_id")
    val reserverId : User,
    @ManyToOne
    @JoinColumn(name = "show_id")
    val showId : Show,
    @Column
    var seat : Long,
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.PENDING,
    @Column
    var isDeleted : Boolean = false
) : TimeStamped() {

    fun softDelete() {
        isDeleted = true
    }

}



