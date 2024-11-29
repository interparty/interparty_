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
import org.springframework.security.crypto.password.PasswordEncoder
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Configuration
class DataInsertConfig(passwordEncoder: PasswordEncoder) {

    @Bean
    fun dataInserter(
        userRepository: UserRepository,
        showRepository: ShowRepository, passwordEncoder: PasswordEncoder
    ): ApplicationRunner {
        return ApplicationRunner {
            // 1. 유저 데이터 삽입
            val users = (1..100).map { i ->
                User(
                    username = "user$i",
                    password = passwordEncoder.encode("password$i"),
                    email = "user$i@example.com",
                    nickname = "User$i",
                    userRole = UserRole.USER,
                    phoneNumber = "010-0000-000$i"
                )
            }
            userRepository.saveAll(users)

            // 2. 유저 데이터를 CSV로 저장
            saveUsersToCsv(users)

            // 3. 공연 데이터 삽입
            val shows = (1..10000).map { i ->
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

            saveUsersToCsv(users)
            saveShowsToCsv(shows)
        }
    }

    private fun saveUsersToCsv(users: List<User>) {
        val fileName = "users.csv"
        val csvData = mutableListOf<String>()

        // 헤더 추가
        csvData.add("userId,username,password,email,nickname,userRole,phoneNumber")

        // 유저 데이터를 CSV 형식으로 변환
        users.forEach { user ->
            csvData.add("${user.id},${user.username},${user.password},${user.email},${user.nickname},${user.userRole},${user.phoneNumber}")
        }

        // 파일로 저장
        File(fileName).writeText(csvData.joinToString("\n"))

        println("유저 데이터가 $fileName 파일로 저장되었습니다.")
    }

    private fun saveShowsToCsv(shows: List<Show>) {
        val fileName = "shows.csv"
        val csvData = mutableListOf<String>()

        // 헤더 추가
        csvData.add("showId,name,contents,address,price,totalSeats,startDateTime,category,managerId")

        // 공연 데이터를 CSV 형식으로 변환
        shows.forEach { show ->
            csvData.add(
                "${show.id},${show.name},${show.contents},${show.address},${show.price}," +
                        "${show.totalSeats},${show.startDateTime},${show.category}," +
                        "${show.manager?.id ?: "null"}"
            )
        }

        // 파일로 저장
        File(fileName).writeText(csvData.joinToString("\n"))

        println("공연 데이터가 $fileName 파일로 저장되었습니다.")
    }

}