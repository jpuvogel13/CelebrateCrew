package CelebrationCrew.demo.repository;

import CelebrationCrew.demo.entity.Profile;
import CelebrationCrew.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

    @Query("SELECT p FROM Profile p WHERE " +
            "FUNCTION('DAY', p.birthDate) >= FUNCTION('DAY', :startDate) AND " +
            "FUNCTION('MONTH', p.birthDate) >= FUNCTION('MONTH', :startDate) AND " +
            "FUNCTION('DAY', p.birthDate) <= FUNCTION('DAY', :endDate) AND " +
            "FUNCTION('MONTH', p.birthDate) <= FUNCTION('MONTH', :endDate)")
    List<Profile> findUpcomingBirthdays(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Profile p WHERE " +
            "FUNCTION('DAY', p.joiningDate) >= FUNCTION('DAY', :startDate) AND " +
            "FUNCTION('MONTH', p.joiningDate) >= FUNCTION('MONTH', :startDate) AND " +
            "FUNCTION('DAY', p.joiningDate) <= FUNCTION('DAY', :endDate) AND " +
            "FUNCTION('MONTH', p.joiningDate) <= FUNCTION('MONTH', :endDate)")
    List<Profile> findUpcomingAnniversaries(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}