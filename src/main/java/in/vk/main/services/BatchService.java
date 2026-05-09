package in.vk.main.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.vk.main.entities.Batch;
import in.vk.main.repositories.BatchRepository;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;

    public void saveBatch(Batch batch) {
        batchRepository.save(batch);
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Batch getBatchById(Long id) {
        return batchRepository.findById(id).orElse(null);
    }

    public void deleteBatchById(Long id) {
        batchRepository.deleteById(id);
    }
}
