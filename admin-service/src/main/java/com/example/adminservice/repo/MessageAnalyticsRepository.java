package com.example.adminservice.repo;

import com.example.adminservice.dto.ChatDurationItemDto;
import com.example.adminservice.model.Message;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageAnalyticsRepository extends JpaRepository<Message, Long> {

    @Query("""
        select new com.example.adminservice.dto.ChatDurationItemDto(
            c.id, c.companyId, c.vacancyId, c.employeeLogin,
            min(m.createdAt), max(m.createdAt), count(m)
        )
        from Message m
        join m.chat c
        where (:companyId is null or c.companyId = :companyId)
        group by c.id, c.companyId, c.vacancyId, c.employeeLogin
    """)
    List<ChatDurationItemDto> findChatDurations(@Param("companyId") Long companyId);

    // time-series (PostgreSQL): day/month/year по created_at чата
    @Query(value = """
    select to_char(date_trunc('day', m.created_at), 'YYYY-MM-DD') as period, count(*) as cnt
    from message m
    join chat c on c.id = m.chat_id
    where (:companyId is null or c.company_id = :companyId)
      and m.created_at >= coalesce(:fromDt, timestamp '1970-01-01 00:00:00')
      and m.created_at <  coalesce(:toDt,   timestamp '2999-12-31 23:59:59')
    group by 1
    order by 1
""", nativeQuery = true)
    List<Object[]> messageCountByDay(@Param("companyId") Long companyId,
                                     @Param("fromDt") LocalDateTime fromDt,
                                     @Param("toDt") LocalDateTime toDt);

    @Query(value = """
    select to_char(date_trunc('month', m.created_at), 'YYYY-MM') as period, count(*) as cnt
    from message m
    join chat c on c.id = m.chat_id
    where (:companyId is null or c.company_id = :companyId)
      and m.created_at >= coalesce(:fromDt, timestamp '1970-01-01 00:00:00')
      and m.created_at <  coalesce(:toDt,   timestamp '2999-12-31 23:59:59')
    group by 1
    order by 1
""", nativeQuery = true)
    List<Object[]> messageCountByMonth(@Param("companyId") Long companyId,
                                       @Param("fromDt") LocalDateTime fromDt,
                                       @Param("toDt") LocalDateTime toDt);

    @Query(value = """
    select to_char(date_trunc('year', m.created_at), 'YYYY') as period, count(*) as cnt
    from message m
    join chat c on c.id = m.chat_id
    where (:companyId is null or c.company_id = :companyId)
      and m.created_at >= coalesce(:fromDt, timestamp '1970-01-01 00:00:00')
      and m.created_at <  coalesce(:toDt,   timestamp '2999-12-31 23:59:59')
    group by 1
    order by 1
""", nativeQuery = true)
    List<Object[]> messageCountByYear(@Param("companyId") Long companyId,
                                      @Param("fromDt") LocalDateTime fromDt,
                                      @Param("toDt") LocalDateTime toDt);



    @Query(value = """
select
  c.id as chat_id,
  comp.name as company_name,
  e.first_name as employee_first_name,
  e.last_name as employee_last_name,
  extract(epoch from (max(m.created_at) - min(m.created_at))) / 60.0 as duration_minutes,
  count(m.id) as messages_count
from message m
join chat c on c.id = m.chat_id
left join company comp on comp.id = c.company_id
left join t_user u on u.login = c.employee_login
left join employee e on e.user_id = u.id
where (:companyId is null or c.company_id = :companyId)
group by c.id, comp.name, e.first_name, e.last_name
order by duration_minutes desc
limit 5
""", nativeQuery = true)
    List<Object[]> findTop5ChatDurationsRaw(@Param("companyId") Long companyId);


}


