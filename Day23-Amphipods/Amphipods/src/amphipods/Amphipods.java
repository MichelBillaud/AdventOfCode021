package amphipods;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Amphipods {

    static enum AmphiType {
        Amber(1), Bronze(10), Copper(100), Desert(1000);

        final int cost;

        private AmphiType(int cost) {
            this.cost = cost;
        }

    }

    static enum RoomType {
        Hallway, Crossing, Room;
    }

    static record Place(RoomType roomType, AmphiType AmphiType) {

    }

    static final class Graph {

        final List<Place> places = new ArrayList<>();
        final Map<Place, List<Place>> neighbours = new HashMap<>();
        final EnumMap<AmphiType, List<Place>> rooms = new EnumMap<>(AmphiType.class);

        Graph(int height) {

            Place extremity = makePlace(RoomType.Hallway, null);
            Place h = makePlace(RoomType.Hallway, null);
            connectPlaces(extremity, h);
            for (var at : AmphiType.values()) {
                rooms.put(at, new ArrayList<>());
                var crossing = makePlace(RoomType.Crossing, at);
                connectPlaces(h, crossing);
                var last = crossing;
                for (int i = 0; i < height; i++) {
                    var room = makePlace(RoomType.Room, at);
                    connectPlaces(last, room);
                    rooms.get(at).add(room);
                    last = room;
                }
                h = makePlace(RoomType.Hallway, at);
                connectPlaces(h, crossing);
            }
            extremity = makePlace(RoomType.Hallway, null);
            connectPlaces(h, extremity);

        }

        private Place makePlace(RoomType rt, AmphiType at) {
            var p = new Place(rt, at);
            places.add(p);
            neighbours.put(p, new ArrayList());
            return p;
        }

        void connectPlaces(Place p1, Place p2) {
            neighbours.get(p1).add(p2);
            neighbours.get(p2).add(p1);
        }
    }

    public static void main(String[] args) {
        var g = new Graph((2));
        g.places.forEach(System.out::println);
        System.out.format("places = %s\n", g.places);

    }

}
