package com.sparta.interparty.domain.show.controller

import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.service.ShowServiceV2
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api/v2/shows")
class ShowControllerV2(
    private val showServiceV2: ShowServiceV2
) {
    @GetMapping("/{id}")
    fun readShowWithCache(@PathVariable id: UUID): Show {
        return showServiceV2.readShowWithCache(id)
    }
}