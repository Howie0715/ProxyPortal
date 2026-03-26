package tw.iehow;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import tw.iehow.data.PortalData;
import tw.iehow.data.PortalManager;

public class PortalCommand {
    private static final String PORTAL_CREATE_SUCCESS = "§aSuccessfully created portal: %s";
    private static final String PORTAL_CREATE_FAILED = "§cFailed to create portal: %s";
    private static final String PORTAL_CREATE_ERROR = "§cError occurred while creating portal: %s";
    private static final String PORTAL_DELETE_SUCCESS = "§aSuccessfully deleted portal: %s";
    private static final String PORTAL_DELETE_NOTFOUND = "§cPortal not found: %s";
    private static final String PORTAL_DELETE_ERROR = "§cError occurred while deleting portal: %s";
    private static final String PORTAL_LIST_EMPTY = "§eNo portals currently exist";
    private static final String PORTAL_LIST_HEADER = "§aPortal List:";
    private static final String PORTAL_LIST_ITEM = "§b- %s";
    private static final String PORTAL_LIST_DIMENSION = "  §7Dimension: %s";
    private static final String PORTAL_LIST_COORDINATES = "  §7Coordinates: (%.1f, %.1f, %.1f) to (%.1f, %.1f, %.1f)";
    private static final String PORTAL_LIST_TARGET_SERVER = "  §7Target Server: %s";
    private static final String PORTAL_LIST_ERROR = "§cError occurred while listing portals: %s";
    private static final String PORTAL_RELOAD_SUCCESS = "§aSuccessfully reloaded portal data";
    private static final String PORTAL_RELOAD_ERROR = "§cError occurred while reloading portals: %s";

    private static final SuggestionProvider<CommandSourceStack> PORTAL_NAME_SUGGESTIONS = (context, builder) -> {
        var portals = PortalManager.getAllPortals();
        for (PortalData portal : portals) {
            builder.suggest(portal.portalName);
        }
        return builder.buildFuture();
    };

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("portal")
            .requires(source -> source.permissions().hasPermission(
                    new Permission.HasCommandLevel(PermissionLevel.byId(2))))
            .then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.string())
                    .then(Commands.argument("x1", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("y1", DoubleArgumentType.doubleArg())
                            .then(Commands.argument("z1", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("x2", DoubleArgumentType.doubleArg())
                                    .then(Commands.argument("y2", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z2", DoubleArgumentType.doubleArg())
                                            .then(Commands.argument("server", StringArgumentType.string())
                                                .executes(PortalCommand::createPortal))))))))))
            .then(Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.string())
                    .suggests(PORTAL_NAME_SUGGESTIONS)
                    .executes(PortalCommand::deletePortal)))
            .then(Commands.literal("list")
                .executes(PortalCommand::listPortals))
            .then(Commands.literal("reload")
                .executes(PortalCommand::reloadPortals))));
    }

    private static int createPortal(CommandContext<CommandSourceStack> context) {
        try {
            String name = StringArgumentType.getString(context, "name");
            double x1 = DoubleArgumentType.getDouble(context, "x1");
            double y1 = DoubleArgumentType.getDouble(context, "y1");
            double z1 = DoubleArgumentType.getDouble(context, "z1");
            double x2 = DoubleArgumentType.getDouble(context, "x2");
            double y2 = DoubleArgumentType.getDouble(context, "y2");
            double z2 = DoubleArgumentType.getDouble(context, "z2");
            String server = StringArgumentType.getString(context, "server");

            CommandSourceStack source = context.getSource();
            String dimension = source.getLevel().dimension().identifier().toString();

            PortalData.Position pos1 = new PortalData.Position(x1, y1, z1);
            PortalData.Position pos2 = new PortalData.Position(x2, y2, z2);
            PortalData.PositionGroup positions = new PortalData.PositionGroup(dimension, pos1, pos2);

            PortalData portalData = new PortalData(name, positions, server);

            boolean success = PortalManager.createPortal(portalData);

            if (success) {
                source.sendSuccess(() -> Component.literal(String.format(PORTAL_CREATE_SUCCESS, name)), false);
            } else {
                source.sendSuccess(() -> Component.literal(String.format(PORTAL_CREATE_FAILED, name)), false);
            }

        } catch (Exception e) {
            context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_CREATE_ERROR, e.getMessage())), false);
            ProxyPortal.LOGGER.error("Failed to create portal", e);
        }

        return 1;
    }

    private static int deletePortal(CommandContext<CommandSourceStack> context) {
        try {
            String name = StringArgumentType.getString(context, "name");

            boolean success = PortalManager.deletePortal(name);

            if (success) {
                context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_DELETE_SUCCESS, name)), false);
            } else {
                context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_DELETE_NOTFOUND, name)), false);
            }

        } catch (Exception e) {
            context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_DELETE_ERROR, e.getMessage())), false);
            ProxyPortal.LOGGER.error("Failed to delete portal", e);
        }

        return 1;
    }

    private static int listPortals(CommandContext<CommandSourceStack> context) {
        try {
            var portals = PortalManager.getAllPortals();

            if (portals.isEmpty()) {
                context.getSource().sendSuccess(() -> Component.literal(PORTAL_LIST_EMPTY), false);
            } else {
                context.getSource().sendSuccess(() -> Component.literal(PORTAL_LIST_HEADER), false);
                for (PortalData portal : portals) {
                    context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_LIST_ITEM, portal.portalName)), false);
                    context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_LIST_DIMENSION, portal.positions.dimension)), false);
                    context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_LIST_COORDINATES,
                        portal.positions.pos1.x, portal.positions.pos1.y, portal.positions.pos1.z,
                        portal.positions.pos2.x, portal.positions.pos2.y, portal.positions.pos2.z)), false);
                    context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_LIST_TARGET_SERVER, portal.destinationServer)), false);

                }
            }

        } catch (Exception e) {
            context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_LIST_ERROR, e.getMessage())), false);
            ProxyPortal.LOGGER.error("Failed to list portals", e);
        }

        return 1;
    }

    private static int reloadPortals(CommandContext<CommandSourceStack> context) {
        try {
            PortalManager.reloadPortals();
            context.getSource().sendSuccess(() -> Component.literal(PORTAL_RELOAD_SUCCESS), false);
            ProxyPortal.LOGGER.info("Reloaded portal data");

        } catch (Exception e) {
            context.getSource().sendSuccess(() -> Component.literal(String.format(PORTAL_RELOAD_ERROR, e.getMessage())), false);
            ProxyPortal.LOGGER.error("Failed to reload portals", e);
        }

        return 1;
    }
}
