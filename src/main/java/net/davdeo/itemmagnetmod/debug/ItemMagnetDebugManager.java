package net.davdeo.itemmagnetmod.debug;

import net.davdeo.itemmagnetmod.ItemMagnetMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ItemMagnetDebugManager {
    private static final long SESSION_IDLE_TICKS = 8L;
    private static final Set<UUID> ENABLED_PLAYERS = new HashSet<>();
    private static final Map<UUID, DebugSnapshot> SNAPSHOTS = new HashMap<>();
    private static final Map<UUID, DebugSession> SESSIONS = new HashMap<>();

    private ItemMagnetDebugManager() {
        super();
    }

    public static boolean setEnabled(ServerPlayer player, boolean enabled) {
        if (enabled) {
            return ENABLED_PLAYERS.add(player.getUUID());
        }

        SNAPSHOTS.remove(player.getUUID());
        SESSIONS.remove(player.getUUID());
        return ENABLED_PLAYERS.remove(player.getUUID());
    }

    public static boolean isEnabled(ServerPlayer player) {
        return ENABLED_PLAYERS.contains(player.getUUID());
    }

    public static void submitSnapshot(ServerPlayer player, int pullLevel, double distance, double desiredSpeed, double maxSpeed, double currentSpeed, boolean closeRangeCapApplied) {
        DebugSnapshot next = new DebugSnapshot(pullLevel, distance, desiredSpeed, maxSpeed, currentSpeed, closeRangeCapApplied);
        DebugSnapshot current = SNAPSHOTS.get(player.getUUID());

        if (current == null || next.distance() < current.distance()) {
            SNAPSHOTS.put(player.getUUID(), next);
        }
    }

    public static void flush(long gameTime, Iterable<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            if (!isEnabled(player)) {
                continue;
            }

            UUID playerId = player.getUUID();
            DebugSnapshot snapshot = SNAPSHOTS.remove(playerId);
            DebugSession session = SESSIONS.get(playerId);

            if (snapshot != null) {
                if (session == null || gameTime - session.lastSeenTick() > SESSION_IDLE_TICKS) {
                    session = new DebugSession(gameTime, gameTime, snapshot.pullLevel());
                } else {
                    session = session.withLastSeenTick(gameTime);
                }

                SESSIONS.put(playerId, session);
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.literal(buildOverlayText(snapshot, gameTime - session.startTick()))
                                .withStyle(ChatFormatting.YELLOW)
                ));
            }

            if (snapshot != null && gameTime % 10L == 0L) {
                ItemMagnetMod.LOGGER.info(
                        "[Item Magnet Debug] player={} pull={} distance={} desired={} max={} current={} closeCap={}",
                        player.getName().getString(),
                        snapshot.pullLevel(),
                        format(snapshot.distance()),
                        format(snapshot.desiredSpeed()),
                        format(snapshot.maxSpeed()),
                        format(snapshot.currentSpeed()),
                        snapshot.closeRangeCapApplied()
                );
            }

            if (session != null && snapshot == null && gameTime - session.lastSeenTick() > SESSION_IDLE_TICKS) {
                double totalSeconds = (session.lastSeenTick() - session.startTick() + 1L) / 20.0;

                ItemMagnetMod.LOGGER.info(
                        "[Item Magnet Debug] player={} pull={} totalTime={}s",
                        player.getName().getString(),
                        session.pullLevel(),
                        format(totalSeconds, 2)
                );

                player.sendSystemMessage(
                        Component.literal("Item Magnet Debug: ")
                                .withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal("Pull " + session.pullLevel()).withStyle(ChatFormatting.AQUA))
                                .append(Component.literal(" total time ").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(format(totalSeconds, 2) + "s").withStyle(ChatFormatting.GREEN))
                );

                SESSIONS.remove(playerId);
            }
        }
    }

    private static String format(double value) {
        return format(value, 3);
    }

    private static String format(double value, int decimals) {
        return String.format(Locale.ROOT, "%." + decimals + "f", value);
    }

    private static String buildOverlayText(DebugSnapshot snapshot, long elapsedTicks) {
        return "Magnet Debug "
                + "L" + snapshot.pullLevel()
                + " t=" + format(elapsedTicks / 20.0, 2) + "s"
                + " d=" + format(snapshot.distance(), 2)
                + " cur=" + format(snapshot.currentSpeed())
                + " des=" + format(snapshot.desiredSpeed())
                + " max=" + format(snapshot.maxSpeed())
                + " cap=" + (snapshot.closeRangeCapApplied() ? "yes" : "no");
    }

    private record DebugSnapshot(
            int pullLevel,
            double distance,
            double desiredSpeed,
            double maxSpeed,
            double currentSpeed,
            boolean closeRangeCapApplied
    ) {
    }

    private record DebugSession(
            long startTick,
            long lastSeenTick,
            int pullLevel
    ) {
        private DebugSession withLastSeenTick(long nextLastSeenTick) {
            return new DebugSession(this.startTick, nextLastSeenTick, this.pullLevel);
        }
    }
}
