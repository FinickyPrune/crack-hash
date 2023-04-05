package ru.nsu.kravchenko.crackhash.worker.model.cracker;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HashCracker {

    public static List<String> crack(String inputHash, Integer length, List<String> alphabet) {
        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(alphabet);
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            Generator<String> gen = CombinatoricsFactory.createPermutationWithRepetitionGenerator(vector, i);
            for (var string : gen) {
                MessageDigest md5;
                try {
                    md5 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                String inputString = String.join("", string.getVector());
                String hash = (new HexBinaryAdapter()).marshal(md5.digest(inputString.getBytes()));
                if (inputHash.equalsIgnoreCase(hash)) {
                    answers.add(String.join("", string.getVector()));
                    log.info("Added answer : {}", String.join("", string.getVector()));
                }
            }
        }

        return answers;
    }

}
