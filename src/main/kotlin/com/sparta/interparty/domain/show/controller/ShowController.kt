package com.sparta.interparty.domain.show.controller

import com.sparta.interparty.domain.show.dto.req.ShowPatchReqDto
import com.sparta.interparty.domain.show.dto.req.ShowPostReqDto
import com.sparta.interparty.domain.show.dto.res.ShowPostResDto
import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.show.mapper.ShowMapper
import com.sparta.interparty.domain.show.mapper.ShowQuery
import com.sparta.interparty.domain.show.service.ShowService
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.global.security.UserDetailsImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/shows")
class ShowController(private val showService: ShowService) {

    @PostMapping
    fun createShow(@AuthenticationPrincipal userDetails: UserDetailsImpl, @RequestBody showPostReqDto: ShowPostReqDto): ResponseEntity<ShowPostResDto> {
        val user: User? = userDetails.getUser()
        var show = ShowMapper.toEntity(showPostReqDto)
        show = showService.createShow(user, show)
        return ResponseEntity.status(HttpStatus.CREATED).body(ShowMapper.toDto(show))
    }

    @GetMapping
    fun readShows(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "startDateTime") sortBy: String,
        @RequestParam(defaultValue = "asc") order: String,
        @RequestParam(required = false) category: ShowCategories?,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) managedBy: UUID?,
        @RequestParam(required = false) dateAfter: LocalDateTime?,
        @RequestParam(required = false) dateBefore: LocalDateTime?
    ): ResponseEntity<List<ShowPostResDto>> {
        val query = ShowQuery(
            page = page,
            size = size,
            sortBy = sortBy,
            order = order,
            category = category,
            search = search,
            managedBy = managedBy,
            dateAfter = dateAfter,
            dateBefore = dateBefore
        )
        val shows: List<Show> = showService.readShows(query)
        return ResponseEntity.ok(shows.map { ShowMapper.toDto(it) })
    }

    @GetMapping("{id}")
    fun readShow(@PathVariable id: UUID): ResponseEntity<ShowPostResDto> {
        val show: Show = showService.readShow(id)
        return ResponseEntity.ok(ShowMapper.toDto(show))
    }

    @PatchMapping("{id}")
    fun updateShow(
        @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: UUID,
        @RequestBody @Valid showPatchReqDto: ShowPatchReqDto
    ): ResponseEntity<ShowPostResDto> {
        val user: User? = userDetails.getUser()
        val patchMap = ShowMapper.toMap(showPatchReqDto)
        val reqShow = showService.updateShow(user, id, patchMap)
        return ResponseEntity.ok(ShowMapper.toDto(reqShow))
    }

    @DeleteMapping("{id}")
    fun deleteShow(@AuthenticationPrincipal userDetails: UserDetailsImpl, @PathVariable id: UUID): ResponseEntity<Unit> {
        val user: User? = userDetails.getUser()
        showService.deleteShow(user, id)
        return ResponseEntity.noContent().build()
    }

}