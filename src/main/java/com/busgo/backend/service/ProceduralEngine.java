package com.busgo.backend.service;
import org.springframework.stereotype.Service;
import java.util.Random;
@Service
public class ProceduralEngine {
    public Random getDeterministicRandom(String... inputs) {
        long seed = 0;
        for (String in : inputs) {
            seed = 31 * seed + in.hashCode();
        }
        return new Random(seed);
    }
}
