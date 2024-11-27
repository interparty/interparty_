package com.sparta.interparty.domain.reservation.controller

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.dto.res.ReservationResDto
import com.sparta.interparty.domain.reservation.service.ReservationService
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.global.security.UserDetailsImpl
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import org.springframework.web.bind.annotation.*
import java.util.*
@RestController
@RequestMapping("/api")
class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping()
    fun createReservation(@PathVariable userId:UUID, @PathVariable showId:UUID,@RequestBody request : ReservationReqDto) : ResponseEntity<ReservationResDto>{
        val response = reservationService.createReservation(userId,showId,request)

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{userId}/reservations")
    fun getMyReservations(@PathVariable userId: UUID,@RequestParam(defaultValue = "0") page: Int,@RequestParam(defaultValue = "10") size: Int)
            : ResponseEntity<Page<ReservationResDto>> {
        val reservations = reservationService.getReservations(userId, page, size)

        return ResponseEntity.status(HttpStatus.OK).body(reservations)
    }

    @DeleteMapping()
    fun softDeleteReservataion(@PathVariable userId: UUID,@PathVariable Id: UUID):ResponseEntity<Void>{
        reservationService.softDeleteReservation(userId,Id)
        return ResponseEntity.noContent().build()
    }
}