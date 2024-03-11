package com.invoiceq.oracleebsadapter.scheduler;

import com.invoiceq.oracleebsadapter.model.NodeLogger;
import com.invoiceq.oracleebsadapter.repository.NodeLoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NodeLoggerService {

    @Autowired
    private NodeLoggerRepository nodeLoggerRepository;
    public boolean isEnabledToRun(String serviceCode){
        boolean enabledToRun = false;
        LocalDateTime dateTimeBeforeTwoMinutes = LocalDateTime.now().minusMinutes(3);
        Optional<NodeLogger> activeNode = nodeLoggerRepository.findFirstByOrderByLastActiveOnDesc();
        if((activeNode.isPresent() && activeNode.get().getServiceCode().equals(serviceCode)) || (activeNode.isPresent() && activeNode.get().getLastActiveOn().isBefore(dateTimeBeforeTwoMinutes)) || activeNode.isEmpty()){
            saveOrUpdateIfExist(serviceCode);
            enabledToRun = true;
        }
        return enabledToRun;
    }

    private void saveOrUpdateIfExist(String serviceCode){
        NodeLogger node = nodeLoggerRepository.findNodeLoggerByServiceCode(serviceCode)
                .orElse(NodeLogger.builder().serviceCode(serviceCode).build());
        node.setLastActiveOn(LocalDateTime.now());
        nodeLoggerRepository.save(node);
    }


}
