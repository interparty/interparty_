package com.sparta.interparty.global.config

import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.entity.UserRole
import com.sparta.interparty.domain.user.repo.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class DataInsertConfig {

    @Bean
    fun dataInserter(
        userRepository: UserRepository,
        showRepository: ShowRepository
    ): ApplicationRunner {
        return ApplicationRunner {
            // 1. 유저 데이터 삽입
            val users = (1..100).map { i ->
                User(
                    username = "user$i",
                    password = "password$i",
                    email = "user$i@example.com",
                    nickname = "User$i",
                    userRole = UserRole.USER,
                    phoneNumber = "010-0000-000$i"
                )
            }
            userRepository.saveAll(users)

            // 2. 공연 데이터 삽입
            val shows = (1..20).map { i ->
                Show(
                    name = "Show $i",
                    contents = "Contents for Show $i",
                    address = "Address $i",
                    price = (1000L * i),
                    totalSeats = 100L,
                    startDateTime = LocalDateTime.now().plusDays(i.toLong()),
                    category = ShowCategories.ETC,
                    manager = users.random() // 랜덤 매니저 할당
                )
            }
            showRepository.saveAll(shows)

            println("100명의 유저와 20개의 공연 데이터가 성공적으로 삽입되었습니다!")
        }
    }
}
