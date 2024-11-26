package com.sparta.interparty.domain.show.service

import com.sparta.interparty.domain.show.entity.Show
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.show.repo.ShowRepository
import com.sparta.interparty.domain.user.entity.User
import com.sparta.interparty.domain.user.entity.UserRole
import com.sparta.interparty.global.exception.CustomException
import com.sparta.interparty.global.exception.ExceptionResponseStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ShowService(val showRepository: ShowRepository) {

    /**
     * 로그인한 유저가 매니저 역할인 경우에만, 새로이 담당하는 공연을 생성함.
     * @param user: User? - 로그인한 유저
     * @param reqShow: Show - 생성할 공연 정보
     * @return Show - 생성된 공연
     */
    @Transactional
    fun createShow(user: User?, reqShow: Show): Show {

        // 상위 레이어에서 전달받은 유저는 로그인되지 않은 경우 null 이므로, 확인 후 예외를 던짐.
        user?: throw CustomException(ExceptionResponseStatus.LOGIN_REQUIRED)

        // 유저의 권한이 매니저가 아닌 경우 예외를 던짐.
        if (user.userRole != UserRole.MANAGER) {
            throw CustomException(ExceptionResponseStatus.MANAGER_ROLE_REQUIRED)
        }

        // Dto 에서 생성한 공연 엔티티의 빈 매니저 자리를 채워 넣고 저장.
        reqShow.manager = user
        return showRepository.save(reqShow)
    }

    /**
     * 공연의 목록 조회를 처리하는 유일한 메서드. 인가 필요 없음.
     * @param page: Int - 페이지 번호
     * @param size: Int - 페이지 크기
     * @param category: String? - 카테고리
     * @param search: String? - 검색어
     * @param managedBy: String? - 매니저 ID
     * @param sortBy: String? - 정렬 기준 (공연의 속성)
     * @param order: String? - 정렬 순서 (asc, desc)
     * @param dateAfter: String? - 기준일 이후로 검색
     * @param dateBefore: String? - 기준일 이전으로 검색
     * @return List<Show> - 조회된 공연 목록
     */
    fun readShows(
        page: Int,
        size: Int,
        sortBy: String,
        order: String,
        category: ShowCategories?,
        search: String?,
        managedBy: UUID?,
        dateAfter: LocalDateTime?,
        dateBefore: LocalDateTime?): List<Show> {
        val pageable = Pageable.ofSize(size).withPage(page - 1)

        // todo - 구현 덜 된 DSL 메서드
        // showRepository.findAllByIsDeletedFalseWithParams(pageable, sortBy, order, category, search, managedBy, dateAfter, dateBefore)

        // 임시로 전체 조회하는 메서드로 대체
        return showRepository.findAllByIsDeletedFalse()
    }

    fun readShow(id: UUID): Show {
        return showRepository.findByIdAndIsDeletedFalse(id).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }
    }

    /**
     * 공연 데이터의 일부 필드만 수정할 수 있는 메서드.
     * 로그인한 유저가 공연 담당 매니저이거나, 어드민인 경우에만 허용됨.
     * @param user: User? - 로그인한 유저
     * @param id: UUID - 수정할 공연 ID
     * @param patchMap: Map<String, Any> - 수정할 필드와 값에 대한 POJO 파라미터
     * @return Show - 수정된 공연
     */
    @Transactional
    fun updateShow(user: User?, id: UUID, patchMap: Map<String, Any>): Show {

        // 상위 레이어에서 전달받은 유저는 로그인되지 않은 경우 null 이므로, 확인 후 예외를 던짐.
        user?: throw CustomException(ExceptionResponseStatus.LOGIN_REQUIRED)

        // 해당 id 를 가진 공연이 존재하지 않는 경우 예외를 던짐.
        val show: Show = showRepository.findByIdAndIsDeletedFalse(id).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        // 해당 공연 매니저가 아니면서 어드민도 아닌 경우 예외를 던짐.
        if (user.id != show.manager?.id && user.userRole != UserRole.ADMIN) {
            throw CustomException(ExceptionResponseStatus.NOT_A_MANAGER)
        }

        // 패치 맵이 비어있을 경우 예외를 던짐.
        if (patchMap.isEmpty()) { throw CustomException(ExceptionResponseStatus.NO_FIELD_MODIFIED) }

        // 패치 맵의 키와 값으로 영속 상태인 공연 엔티티를 수정.
        try {
            patchMap.forEach { (key, value) ->
                when (key) {
                    "name" -> show.name = (value as String)
                    "contents" -> show.contents = (value as String)
                    "address" -> show.address = (value as String)
                    "price" -> show.price = (value as Long)
                    "totalSeats" -> show.totalSeats = (value as Long)
                    "startDateTime" -> show.startDateTime = (LocalDateTime.parse(value as String))
                    "category" -> show.category = (value as ShowCategories)
                }
            }
        } catch (ex: Exception) {
            throw CustomException(ExceptionResponseStatus.INVALID_FIELD_MODIFICATION)
        }

        return showRepository.save(show)
    }

    /**
     * 공연을 Soft Delete 하는 메서드.
     * 로그인한 유저가 공연 담당 매니저이거나, 어드민인 경우에만 허용됨.
     * @param user: User? - 로그인한 유저
     * @param id: UUID - 삭제할 공연 ID
     */
    @Transactional
    fun deleteShow(user: User?, id: UUID) {

        // 상위 레이어에서 전달받은 유저는 로그인되지 않은 경우 null 이므로, 확인 후 예외를 던짐.
        user?: throw CustomException(ExceptionResponseStatus.LOGIN_REQUIRED)

        // 해당 id 를 가진 공연이 존재하지 않는 경우 예외를 던짐.
        val show: Show = showRepository.findByIdAndIsDeletedFalse(id).orElseThrow { CustomException(ExceptionResponseStatus.SHOW_NOT_FOUND) }

        // 해당 공연 매니저가 아니면서 어드민도 아닌 경우 예외를 던짐.
        if (user.id != show.manager?.id && user.userRole != UserRole.ADMIN) {
            throw CustomException(ExceptionResponseStatus.NOT_A_MANAGER)
        }

        // Soft Delete 처리 후 저장.
        show.isDeleted = true
        showRepository.save(show)
    }

}