package com.drakkarpress.platform.service;

import com.drakkarpress.platform.model.BookGenerationJob;
import com.drakkarpress.platform.model.PublicationJob;
import com.drakkarpress.platform.repository.BookGenerationJobRepository;
import com.drakkarpress.platform.repository.PublicationJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobStatsService {

    private final BookGenerationJobRepository bookRepo;
    private final PublicationJobRepository pubRepo;

    public Map<String,Object> getStats() {
        Map<String,Object> m = new HashMap<>();
        m.put("generation.pending", bookRepo.countByStatus(BookGenerationJob.JobStatus.PENDING));
        m.put("generation.generating", bookRepo.countByStatus(BookGenerationJob.JobStatus.GENERATING));
        m.put("generation.completed", bookRepo.countByStatus(BookGenerationJob.JobStatus.COMPLETED));
        m.put("generation.failed", bookRepo.countByStatus(BookGenerationJob.JobStatus.FAILED));
        m.put("publication.pending", pubRepo.countByStatus(PublicationJob.PublicationStatus.PENDING));
        m.put("publication.uploading", pubRepo.countByStatus(PublicationJob.PublicationStatus.UPLOADING));
        m.put("publication.completed", pubRepo.countByStatus(PublicationJob.PublicationStatus.COMPLETED));
        m.put("publication.failed", pubRepo.countByStatus(PublicationJob.PublicationStatus.FAILED));
        // Average duration (seconds) for last completed generation jobs (up to 100)
        var completedGen = bookRepo.findByStatus(BookGenerationJob.JobStatus.COMPLETED);
        double avgGen = completedGen.stream()
            .filter(j -> j.getCreatedAt() != null && j.getCompletedAt() != null)
            .mapToLong(j -> Duration.between(j.getCreatedAt(), j.getCompletedAt()).getSeconds())
            .average().orElse(0);
        var completedPub = pubRepo.findByStatus(PublicationJob.PublicationStatus.COMPLETED);
        double avgPub = completedPub.stream()
            .filter(j -> j.getCreatedAt() != null && j.getCompletedAt() != null)
            .mapToLong(j -> Duration.between(j.getCreatedAt(), j.getCompletedAt()).getSeconds())
            .average().orElse(0);
        m.put("generation.avgSeconds", avgGen);
        m.put("publication.avgSeconds", avgPub);

        long genTotal = (long)m.get("generation.pending") + (long)m.get("generation.generating") + (long)m.get("generation.completed") + (long)m.get("generation.failed");
        long genFailed = (long)m.get("generation.failed");
        m.put("generation.failPercent", genTotal == 0 ? 0.0 : (genFailed * 100.0 / genTotal));

        long pubTotal = (long)m.get("publication.pending") + (long)m.get("publication.uploading") + (long)m.get("publication.completed") + (long)m.get("publication.failed");
        long pubFailed = (long)m.get("publication.failed");
        m.put("publication.failPercent", pubTotal == 0 ? 0.0 : (pubFailed * 100.0 / pubTotal));

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        var recentGen = bookRepo.findByStatus(BookGenerationJob.JobStatus.COMPLETED).stream().filter(j -> j.getCreatedAt()!=null && j.getCreatedAt().isAfter(since)).count();
        var recentGenFailed = bookRepo.findByStatus(BookGenerationJob.JobStatus.FAILED).stream().filter(j -> j.getCreatedAt()!=null && j.getCreatedAt().isAfter(since)).count();
        m.put("generation.last24.completed", recentGen);
        m.put("generation.last24.failed", recentGenFailed);
        var recentPub = pubRepo.findByStatus(PublicationJob.PublicationStatus.COMPLETED).stream().filter(j -> j.getCreatedAt()!=null && j.getCreatedAt().isAfter(since)).count();
        var recentPubFailed = pubRepo.findByStatus(PublicationJob.PublicationStatus.FAILED).stream().filter(j -> j.getCreatedAt()!=null && j.getCreatedAt().isAfter(since)).count();
        m.put("publication.last24.completed", recentPub);
        m.put("publication.last24.failed", recentPubFailed);
        return m;
    }
}
