package com.example.football.service.impl;

import com.example.football.models.dto.xml.StatsRootSeedDto;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.football.util.Constants.*;

//ToDo - Implement all methods
@Service
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public StatServiceImpl(StatRepository statRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.statRepository = statRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(Path.of(STATS_FILEPATH));
    }

    @Override
    public String importStats() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        StatsRootSeedDto statsRootSeedDto = xmlParser.fromFile(STATS_FILEPATH, StatsRootSeedDto.class);

        xmlParser.fromFile(STATS_FILEPATH, StatsRootSeedDto.class)
                .getStats()
                .stream()
                .filter(statsSeedDto -> {

                    float passing = statsSeedDto.getPassing();
                    float shooting = statsSeedDto.getShooting();
                    float endurance = statsSeedDto.getEndurance();

                    boolean isValid = validationUtil.isValid(statsSeedDto)
                            && !statRepository.existsByPassingAndShootingAndEndurance(passing, shooting, endurance);

                    sb.append(isValid
                                    ? String.format(SUCCESSFUL_STAT, shooting, passing, endurance)
                                    : String.format(INVALID, STAT))
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(statsSeedDto -> modelMapper.map(statsSeedDto, Stat.class))
                .forEach(statRepository::save);


        return sb.toString().trim();
    }

    @Override
    public Stat findById(Long id) {
        return statRepository
                .findById(id)
                .orElse(null);
    }
}
