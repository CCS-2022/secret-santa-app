package com.ccs.secretsantaapp.service;

import com.ccs.secretsantaapp.dao.SecretSantaUser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class PairGenerator {
    private static final Logger logger = Logger.getLogger(String.valueOf(PairGenerator.class));
    public Map<SecretSantaUser, SecretSantaUser> generatePairs(List<SecretSantaUser> participants) {
        HashMap<SecretSantaUser, SecretSantaUser> pairs = new HashMap<>();
        Random rand = new Random();
        for(int i = 0; i < participants.size(); i++){
            int randomSpot = rand.nextInt(participants.size());
            swap((ArrayList<SecretSantaUser>) participants, i, randomSpot);
        }

        for(int i = 0; i < participants.size(); i++){
            if(i == participants.size() - 1){
                pairs.put(participants.get(i) , participants.get(0));
            }else{
                pairs.put(participants.get(i), participants.get(i+1));
            }

        }

        printPairs(pairs);
        return pairs;
    }

    private void swap(ArrayList<SecretSantaUser> receivers, int x, int y){
        SecretSantaUser temp = receivers.get(x);
        receivers.set(x , receivers.get(y));
        receivers.set(y, temp);
    }

    private void printPairs(HashMap<SecretSantaUser, SecretSantaUser> pairs){
        for(Map.Entry<SecretSantaUser, SecretSantaUser> entry : pairs.entrySet()){
            logger.info(entry.getKey().getFirstName() + " -> " + entry.getValue().getFirstName());
        }
    }
}
