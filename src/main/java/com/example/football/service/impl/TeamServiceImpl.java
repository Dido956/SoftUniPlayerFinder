package com.example.football.service.impl;

import com.example.football.models.dto.TeamSeedDto;
import com.example.football.models.dto.TownSeedDto;
import com.example.football.models.entity.Team;
import com.example.football.repository.TeamRepository;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static com.example.football.util.Constants.*;

//ToDo - Implement all methods
@Service
public class TeamServiceImpl implements TeamService {

    private final TownService townService;
    private final TeamRepository teamRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    public TeamServiceImpl(TownService townService, TeamRepository teamRepository, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.townService = townService;
        this.teamRepository = teamRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(Path.of(TEAMS_FILEPATH));
    }

    @Override
    public String importTeams() throws IOException {
        StringBuilder sb = new StringBuilder();


        Arrays.stream(gson
                        .fromJson(readTeamsFileContent(), TeamSeedDto[].class))
                .filter(teamSeedDto -> {
                    boolean isValid = validationUtil.isValid(teamSeedDto)
                            && !teamRepository.existsByName(teamSeedDto.getName());

                    sb.append(isValid
                                    ? String.format(SUCCESSFUL_TEAM, teamSeedDto.getName(), teamSeedDto.getFanBase())
                                    : String.format(INVALID, TEAM))
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(teamSeedDto -> {
                    Team team = modelMapper.map(teamSeedDto, Team.class);
                    team.setTown(townService.findByName(teamSeedDto.getTownName()));

                    return team;
                })
                .forEach(teamRepository::save);

        return sb.toString().trim();
    }

    @Override
    public Team findByTeamName(String name) {
        return teamRepository
                .findByName(name)
                .orElse(null);
    }

    private Team existsByTeamName(String teamSeedDtoName) {
        return teamRepository.findByName(teamSeedDtoName).orElse(null);
    }

}


