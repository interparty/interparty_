package com.sparta.interparty.domain.reservation.controller

import com.sparta.interparty.domain.reservation.dto.req.ReservationReqDto
import com.sparta.interparty.domain.reservation.dto.res.ReservationResDto
import com.sparta.interparty.domain.reservation.service.ReservationService
import com.sparta.interparty.global.security.UserDetailsImpl
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping("/shows/{showId}/reservations") // api/shows/{showId}/reservations
    fun createReservation(
        @AuthenticationPrincipal userDetailsImpl: UserDetailsImpl,
        @PathVariable showId: UUID,
        @RequestBody request: ReservationReqDto
    ) : ResponseEntity<ReservationResDto>{
        val userId: UUID = userDetailsImpl.getUser()?.id!!
        val response = reservationService.createReservation(userId,showId,request)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/reservations")
    fun getMyReservations(
        @AuthenticationPrincipal userDetailsImpl: UserDetailsImpl,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) : ResponseEntity<Page<ReservationResDto>> {
        val userId: UUID = userDetailsImpl.getUser()?.id!!
        val reservations = reservationService.getReservations(userId, page, size)

        return ResponseEntity.status(HttpStatus.OK).body(reservations)
    }

    @DeleteMapping("/reservations/{reservationId}") // api/reservations/{reservationId}
    fun softDeleteReservation(
        @AuthenticationPrincipal userDetailsImpl: UserDetailsImpl,
        @PathVariable reservationId: UUID
    ) : ResponseEntity<Unit>{
        val userId: UUID = userDetailsImpl.getUser()?.id!!
        reservationService.softDeleteReservation(userId,reservationId)
        return ResponseEntity.noContent().build()
    }
}