package ru.nsu.kravchenko.crackhash.worker.model.cracker;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;

@Slf4j
public class HashCracker {

    public static List<String> crack(String inputHash, Integer length, Integer partNumber, Integer partCount, List<String> alphabet) {
        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(alphabet);
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            Generator<String> gen = createPermutationWithRepetitionGenerator(vector, i);
            long countPermutations = gen.getNumberOfGeneratedObjects();
            long startIndex = countPermutations / partCount * partNumber;
            if (countPermutations % partCount != 0 ) {
                if (partNumber < countPermutations % partCount){
                    startIndex += partNumber;
                } else if (partNumber >= countPermutations % partCount) {
                    startIndex += countPermutations % partCount;
                }
            }
            var iterator = gen.iterator();
            long stopIndex = startIndex + countPermutations / partCount;
            long index = 1;
            while (iterator.hasNext()) {
                if (index >= startIndex && index <= stopIndex) {

                    var string = iterator.next();
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
                        log.info("added answer : {}", String.join("", string.getVector()));
                    }
                } else if (index > stopIndex) {
                    break;
                } else {
                    iterator.next();
                }
                index++;
            }
            log.info(String.valueOf(answers));

        }

        return answers;
    }

}
