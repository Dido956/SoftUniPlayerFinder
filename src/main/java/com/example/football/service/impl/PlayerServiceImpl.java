package com.example.football.service.impl;

import com.example.football.models.dto.xml.PlayerRootSeedDto;
import com.example.football.models.entity.Player;
import com.example.football.repository.PlayerRepository;
import com.example.football.service.PlayerService;
import com.example.football.service.StatService;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.football.util.Constants.*;

//ToDo - Implement all methods
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final TownService townService;
    private final StatService statService;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public PlayerServiceImpl(PlayerRepository playerRepository, TeamService teamService, TownService townService, StatService statService, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
        this.townService = townService;
        this.statService = statService;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYERS_FILEPATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        PlayerRootSeedDto playerRootSeedDto = xmlParser.fromFile(PLAYERS_FILEPATH, PlayerRootSeedDto.class);

        xmlParser.fromFile(PLAYERS_FILEPATH, PlayerRootSeedDto.class)
                .getPlayers()
                .stream()
                .filter(playerSeedDto -> {
                    boolean isValid = validationUtil.isValid(playerSeedDto)
                            && !playerRepository.existsByEmail(playerSeedDto.getEmail());

                    sb.append(isValid
                                    ? String.format(SUCCESSFUL_PLAYER,
                                    playerSeedDto.getFirstName(),
                                    playerSeedDto.getLastName(),
                                    playerSeedDto.getPosition().name())
                                    : String.format(INVALID, PLAYER))
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(playerSeedDto -> {
                    Player player = modelMapper.map(playerSeedDto, Player.class);
                    player.setTown(townService.findByName(playerSeedDto.getTown().getName()));
                    player.setTeam(teamService.findByTeamName(playerSeedDto.getTeam().getName()));
                    player.setStat(statService.findById(playerSeedDto.getStat().getId()));

                    return player;
                })
                .forEach(playerRepository::save);


        return sb.toString().trim();
    }

    @Override
    public String exportBestPlayers() {
        LocalDate lower = LocalDate.of(1995, 1, 1);
        LocalDate upper = LocalDate.of(2003, 1, 1);

        List<Player> players = playerRepository
                .findByBirthDateBetweenOrderByStatShootingDescStatPassingDescStatEnduranceDescLastNameAsc(lower, upper);

        return players
                        .stream()
                        .map(Player::toString)
                        .collect(Collectors.joining("\n"));
    }

}
