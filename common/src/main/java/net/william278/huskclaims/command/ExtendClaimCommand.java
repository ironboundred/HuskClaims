package net.william278.huskclaims.command;

import lombok.AllArgsConstructor;
import net.william278.huskclaims.HuskClaims;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.claim.ClaimWorld;
import net.william278.huskclaims.claim.ClaimingMode;
import net.william278.huskclaims.claim.Region;
import net.william278.huskclaims.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ExtendClaimCommand extends InClaimCommand {

    protected ExtendClaimCommand(@NotNull HuskClaims plugin) {
        super(
                List.of("extendclaim"),
                "<blocks>",
                null,
                plugin
        );
    }

    @Override
    public void execute(@NotNull OnlineUser executor, @NotNull ClaimWorld world,
                        @NotNull Claim claim, @NotNull String[] args) {
        if ((claim.getOwner().isEmpty() && !ClaimingMode.ADMIN_CLAIMS.canUse(executor))
                || claim.getOwner().map(owner -> owner.equals(executor.getUuid())).orElse(false)) {
            plugin.getLocales().getLocale("no_resizing_permission")
                    .ifPresent(executor::sendMessage);
            return;
        }

        final Optional<Integer> extendAmount = parseIntArg(args, 0);
        if (extendAmount.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        final ExtendDirection facing = ExtendDirection.getFrom(executor.getPosition().getYaw());
        //todo this is not working lol
        int north = 0, south = 0, east = 0, west = 0;
        switch (facing) {
            case NORTH -> north = extendAmount.get();
            case SOUTH -> south = extendAmount.get();
            case EAST -> east = extendAmount.get();
            case WEST -> west = extendAmount.get();
        }
        final Region resized = claim.getRegion().getResized(north, south, east, west);
        plugin.userResizeClaim(executor, world, claim, resized);
    }

    @AllArgsConstructor
    private enum ExtendDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        @NotNull
        private static ExtendDirection getFrom(float yaw) {
            yaw = yaw % 360;
            if (yaw < 0) {
                yaw += 360.0f;
            }
            if (yaw >= 315 || yaw < 45) {
                return SOUTH;
            } else if (yaw < 135) {
                return WEST;
            } else if (yaw < 225) {
                return NORTH;
            } else {
                return EAST;
            }
        }
    }

}