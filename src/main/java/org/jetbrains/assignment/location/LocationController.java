package org.jetbrains.assignment.location;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @PostMapping("/locations")
    public List<Coordinate> locations(@RequestBody List<Move> locations) {
        int x = 0;
        int y = 0;

        Integer maxLocationOrder = repository.findMaxLocationOrder();
        if (maxLocationOrder != null) {
            LocationEntity latest =repository.findFirstByLocationOrder(maxLocationOrder);
            x = latest.getX();
            y = latest.getY();
        }


        List<Coordinate> result = new ArrayList<>();
        result.add(new Coordinate(x, y));
        for (Move location : locations) {
            switch (location.getDirection()) {
                case "EAST":
                    x += location.getSteps();
                    break;
                case "NORTH":
                    y += location.getSteps();
                    break;
                case "SOUTH":
                    y -= location.getSteps();
                    break;
                case "WEST":
                    x -= location.getSteps();
                    break;
            }
            LocationEntity entity = fromModel(location, x, y);
            repository.save(entity);
            result.add(new Coordinate(entity.getX(), entity.getY()));
        }

        return result;
    }

    @PostMapping("/moves")
    public List<Move> moves(@RequestBody List<Coordinate> coordinates) {
        int x = coordinates.get(0).getX();
        int y = coordinates.get(0).getY();

        List<Move> moves = new ArrayList<>();
        for (int i = 1; i < coordinates.size(); i++) {
            Coordinate next = coordinates.get(i);
            if (next.getX() > x) {
                Move move = new Move();
                move.setDirection("EAST");
                move.setSteps(next.getX() - x);
                moves.add(move);
            }
            if (next.getX() < x) {
                Move move = new Move();
                move.setDirection("WEST");
                move.setSteps(x - next.getX());
                moves.add(move);
            }
            if (next.getY() > y) {
                Move move = new Move();
                move.setDirection("NORTH");
                move.setSteps(next.getY() - y);
                moves.add(move);
            }
            if (next.getY() < y) {
                Move move = new Move();
                move.setDirection("SOUTH");
                move.setSteps(y - next.getY());
                moves.add(move);
            }

            x = next.getX();
            y = next.getY();
        }

        return moves;
    }

    private static LocationEntity fromModel(Move location, int x, int y) {
        LocationEntity entity = new LocationEntity();
        entity.setDirection(location.getDirection());
        entity.setStep(location.getSteps());
        entity.setX(x);
        entity.setY(y);
        return entity;
    }
}
