//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jagrosh.jdautilities.command.impl;

import com.jagrosh.jdautilities.command.AnnotatedModuleCompiler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import com.jagrosh.jdautilities.command.GuildSettingsManager;
import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.commons.utils.FixedSizeCache;
import com.jagrosh.jdautilities.commons.utils.SafeIdUtil;
import java.io.IOException;
import java.io.Reader;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request.Builder;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandClientImpl implements CommandClient, EventListener {
    private static final Logger LOG = LoggerFactory.getLogger(CommandClient.class);
    private static final String DEFAULT_PREFIX = "@mention";
    private final OffsetDateTime start;
    private final Activity activity;
    private final OnlineStatus status;
    private final String ownerId;
    private final String[] coOwnerIds;
    private final String prefix;
    private final String altprefix;
    private final String serverInvite;
    private final HashMap<String, Integer> commandIndex;
    private final ArrayList<Command> commands;
    private final String success;
    private final String warning;
    private final String error;
    private final String botsKey;
    private final String carbonKey;
    private final HashMap<String, OffsetDateTime> cooldowns;
    private final HashMap<String, Integer> uses;
    private final FixedSizeCache<Long, Set<Message>> linkMap;
    private final boolean useHelp;
    private final boolean shutdownAutomatically;
    private final Consumer<CommandEvent> helpConsumer;
    private final String helpWord;
    private final ScheduledExecutorService executor;
    private final AnnotatedModuleCompiler compiler;
    private final GuildSettingsManager manager;
    private String textPrefix;
    private CommandListener listener = null;
    private int totalGuilds;

    public CommandClientImpl(String ownerId, String[] coOwnerIds, String prefix, String altprefix, Activity activity, OnlineStatus status, String serverInvite, String success, String warning, String error, String carbonKey, String botsKey, ArrayList<Command> commands, boolean useHelp, boolean shutdownAutomatically, Consumer<CommandEvent> helpConsumer, String helpWord, ScheduledExecutorService executor, int linkedCacheSize, AnnotatedModuleCompiler compiler, GuildSettingsManager manager) {
        Checks.check(ownerId != null, "Owner ID was set null or not set! Please provide an User ID to register as the owner!");
        if (!SafeIdUtil.checkId(ownerId)) {
            LOG.warn(String.format("The provided Owner ID (%s) was found unsafe! Make sure ID is a non-negative long!", ownerId));
        }

        if (coOwnerIds != null) {
            String[] var22 = coOwnerIds;
            int var23 = coOwnerIds.length;

            for(int var24 = 0; var24 < var23; ++var24) {
                String coOwnerId = var22[var24];
                if (!SafeIdUtil.checkId(coOwnerId)) {
                    LOG.warn(String.format("The provided CoOwner ID (%s) was found unsafe! Make sure ID is a non-negative long!", coOwnerId));
                }
            }
        }

        this.start = OffsetDateTime.now();
        this.ownerId = ownerId;
        this.coOwnerIds = coOwnerIds;
        this.prefix = prefix != null && !prefix.isEmpty() ? prefix : "@mention";
        this.altprefix = altprefix != null && !altprefix.isEmpty() ? altprefix : null;
        this.textPrefix = prefix;
        this.activity = activity;
        this.status = status;
        this.serverInvite = serverInvite;
        this.success = success == null ? "" : success;
        this.warning = warning == null ? "" : warning;
        this.error = error == null ? "" : error;
        this.carbonKey = carbonKey;
        this.botsKey = botsKey;
        this.commandIndex = new HashMap();
        this.commands = new ArrayList();
        this.cooldowns = new HashMap();
        this.uses = new HashMap();
        this.linkMap = linkedCacheSize > 0 ? new FixedSizeCache(linkedCacheSize) : null;
        this.useHelp = useHelp;
        this.shutdownAutomatically = shutdownAutomatically;
        this.helpWord = helpWord == null ? "help" : helpWord;
        this.executor = executor == null ? Executors.newSingleThreadScheduledExecutor() : executor;
        this.compiler = compiler;
        this.manager = manager;
        this.helpConsumer = helpConsumer == null ? (event) -> {
            StringBuilder builder = new StringBuilder("**" + event.getSelfUser().getName() + "** commands:\n");
            Category category = null;
            Iterator var8 = commands.iterator();

            while(true) {
                Command command;
                do {
                    do {
                        if (!var8.hasNext()) {
                            User owner = event.getJDA().getUserById(ownerId);
                            if (owner != null) {
                                builder.append("\n\nFor additional help, contact **").append(owner.getName()).append("**#").append(owner.getDiscriminator());
                                if (serverInvite != null) {
                                    builder.append(" or join ").append(serverInvite);
                                }
                            }

                            event.replyInDm(builder.toString(), (unused) -> {
                                if (event.isFromType(ChannelType.TEXT)) {
                                    event.reactSuccess();
                                }

                            }, (t) -> {
                                event.replyWarning("Help cannot be sent because you are blocking Direct Messages.");
                            });
                            return;
                        }

                        command = (Command)var8.next();
                    } while(command.isHidden());
                } while(command.isOwnerCommand() && !event.isOwner());

                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    builder.append("\n\n  __").append(category == null ? "No Category" : category.getName()).append("__:\n");
                }

                builder.append("\n`").append(this.textPrefix).append(prefix == null ? " " : "").append(command.getName()).append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`").append(" - ").append(command.getHelp());
            }
        } : helpConsumer;
        Iterator var26 = commands.iterator();

        while(var26.hasNext()) {
            Command command = (Command)var26.next();
            this.addCommand(command);
        }

    }

    public void setListener(CommandListener listener) {
        this.listener = listener;
    }

    public CommandListener getListener() {
        return this.listener;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public OffsetDateTime getStartTime() {
        return this.start;
    }

    public OffsetDateTime getCooldown(String name) {
        return (OffsetDateTime)this.cooldowns.get(name);
    }

    public int getRemainingCooldown(String name) {
        if (this.cooldowns.containsKey(name)) {
            int time = (int)Math.ceil((double)OffsetDateTime.now().until((Temporal)this.cooldowns.get(name), ChronoUnit.MILLIS) / 1000.0D);
            if (time <= 0) {
                this.cooldowns.remove(name);
                return 0;
            } else {
                return time;
            }
        } else {
            return 0;
        }
    }

    public void applyCooldown(String name, int seconds) {
        this.cooldowns.put(name, OffsetDateTime.now().plusSeconds((long)seconds));
    }

    public void cleanCooldowns() {
        OffsetDateTime now = OffsetDateTime.now();
        List var10000 = (List)this.cooldowns.keySet().stream().filter((str) -> {
            return ((OffsetDateTime)this.cooldowns.get(str)).isBefore(now);
        }).collect(Collectors.toList());
        HashMap var10001 = this.cooldowns;
        var10000.forEach(var10001::remove);
    }

    public int getCommandUses(Command command) {
        return this.getCommandUses(command.getName());
    }

    public int getCommandUses(String name) {
        return (Integer)this.uses.getOrDefault(name, 0);
    }

    public void addCommand(Command command) {
        this.addCommand(command, this.commands.size());
    }

    public void addCommand(Command command, int index) {
        if (index <= this.commands.size() && index >= 0) {
            synchronized(this.commandIndex) {
                String name = command.getName().toLowerCase();
                if (this.commandIndex.containsKey(name)) {
                    throw new IllegalArgumentException("Command added has a name or alias that has already been indexed: \"" + name + "\"!");
                }

                String[] var5 = command.getAliases();
                int var6 = var5.length;

                int var7;
                String alias;
                for(var7 = 0; var7 < var6; ++var7) {
                    alias = var5[var7];
                    if (this.commandIndex.containsKey(alias.toLowerCase())) {
                        throw new IllegalArgumentException("Command added has a name or alias that has already been indexed: \"" + alias + "\"!");
                    }
                }

                if (index < this.commands.size()) {
//                    ((List)this.commandIndex.entrySet().stream().filter((entry) -> {
//                        return (Integer)entry.getValue() >= index;
//                    }).collect(Collectors.toList())).forEach((entry) -> {
//                        Integer var10000 = (Integer)this.commandIndex.put(entry.getKey(), (Integer)entry.getValue() + 1);
//                    });1
                }

                this.commandIndex.put(name, index);
                var5 = command.getAliases();
                var6 = var5.length;
                var7 = 0;

                while(true) {
                    if (var7 >= var6) {
                        break;
                    }

                    alias = var5[var7];
                    this.commandIndex.put(alias.toLowerCase(), index);
                    ++var7;
                }
            }

            this.commands.add(index, command);
        } else {
            throw new ArrayIndexOutOfBoundsException("Index specified is invalid: [" + index + "/" + this.commands.size() + "]");
        }
    }

    public void removeCommand(String name) {
        synchronized(this.commandIndex) {
            if (!this.commandIndex.containsKey(name.toLowerCase())) {
                throw new IllegalArgumentException("Name provided is not indexed: \"" + name + "\"!");
            } else {
                int targetIndex = (Integer)this.commandIndex.remove(name.toLowerCase());
                Command removedCommand = (Command)this.commands.remove(targetIndex);
                String[] var5 = removedCommand.getAliases();
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String alias = var5[var7];
                    this.commandIndex.remove(alias.toLowerCase());
                }

//                ((List)this.commandIndex.entrySet().stream().filter((entry) -> {
//                    return (Integer)entry.getValue() > targetIndex;
//                }).collect(Collectors.toList())).forEach((entry) -> {
//                    Integer var10000 = (Integer)this.commandIndex.put(entry.getKey(), (Integer)entry.getValue() - 1);
//                });
            }
        }
    }

    public void addAnnotatedModule(Object module) {
        this.compiler.compile(module).forEach(this::addCommand);
    }

    public void addAnnotatedModule(Object module, Function<Command, Integer> mapFunction) {
        this.compiler.compile(module).forEach((command) -> {
            this.addCommand(command, (Integer)mapFunction.apply(command));
        });
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public long getOwnerIdLong() {
        return Long.parseLong(this.ownerId);
    }

    public String[] getCoOwnerIds() {
        return this.coOwnerIds;
    }

    public long[] getCoOwnerIdsLong() {
        if (this.coOwnerIds == null) {
            return null;
        } else {
            long[] ids = new long[this.coOwnerIds.length];

            for(int i = 0; i < ids.length; ++i) {
                ids[i] = Long.parseLong(this.coOwnerIds[i]);
            }

            return ids;
        }
    }

    public String getSuccess() {
        return this.success;
    }

    public String getWarning() {
        return this.warning;
    }

    public String getError() {
        return this.error;
    }

    public ScheduledExecutorService getScheduleExecutor() {
        return this.executor;
    }

    public String getServerInvite() {
        return this.serverInvite;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getAltPrefix() {
        return this.altprefix;
    }

    public String getTextualPrefix() {
        return this.textPrefix;
    }

    public int getTotalGuilds() {
        return this.totalGuilds;
    }

    public String getHelpWord() {
        return this.helpWord;
    }

    public boolean usesLinkedDeletion() {
        return this.linkMap != null;
    }

    public <S> S getSettingsFor(Guild guild) {
        return this.manager == null ? null : (S) this.manager.getSettings(guild);
    }

    public <M extends GuildSettingsManager> M getSettingsManager() {
        return (M) this.manager;
    }

    public void shutdown() {
        GuildSettingsManager<?> manager = this.getSettingsManager();
        if (manager != null) {
            manager.shutdown();
        }

        this.executor.shutdown();
    }

    public void onEvent(GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            this.onMessageReceived((MessageReceivedEvent)event);
        } else if (event instanceof GuildMessageDeleteEvent && this.usesLinkedDeletion()) {
            this.onMessageDelete((GuildMessageDeleteEvent)event);
        } else if (event instanceof GuildJoinEvent) {
            if (((GuildJoinEvent)event).getGuild().getSelfMember().getTimeJoined().plusMinutes(10L).isAfter(OffsetDateTime.now())) {
                this.sendStats(event.getJDA());
            }
        } else if (event instanceof GuildLeaveEvent) {
            this.sendStats(event.getJDA());
        } else if (event instanceof ReadyEvent) {
            this.onReady((ReadyEvent)event);
        } else if (event instanceof ShutdownEvent && this.shutdownAutomatically) {
            this.shutdown();
        }

    }

    private void onReady(ReadyEvent event) {
        if (!event.getJDA().getSelfUser().isBot()) {
            LOG.error("JDA-Utilities does not support CLIENT accounts.");
            event.getJDA().shutdown();
        } else {
            this.textPrefix = this.prefix.equals("@mention") ? "@" + event.getJDA().getSelfUser().getName() + " " : this.prefix;
            event.getJDA().getPresence().setPresence(this.status == null ? OnlineStatus.ONLINE : this.status, this.activity == null ? null : ("default".equals(this.activity.getName()) ? Activity.playing("Type " + this.textPrefix + this.helpWord) : this.activity));
            GuildSettingsManager<?> manager = this.getSettingsManager();
            if (manager != null) {
                manager.init();
            }

            this.sendStats(event.getJDA());
        }
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() || true) {
            String[] parts = null;
            String rawContent = event.getMessage().getContentRaw();
            GuildSettingsProvider settings = event.isFromType(ChannelType.TEXT) ? this.provideSettings(event.getGuild()) : null;
            if ((this.prefix.equals("@mention") || this.altprefix != null && this.altprefix.equals("@mention")) && (rawContent.startsWith("<@" + event.getJDA().getSelfUser().getId() + ">") || rawContent.startsWith("<@!" + event.getJDA().getSelfUser().getId() + ">"))) {
                parts = splitOnPrefixLength(rawContent, rawContent.indexOf(">") + 1);
            }

            if (parts == null && rawContent.toLowerCase().startsWith(this.prefix.toLowerCase())) {
                parts = splitOnPrefixLength(rawContent, this.prefix.length());
            }

            if (parts == null && this.altprefix != null && rawContent.toLowerCase().startsWith(this.altprefix.toLowerCase())) {
                parts = splitOnPrefixLength(rawContent, this.altprefix.length());
            }

            if (parts == null && settings != null) {
                Collection<String> prefixes = settings.getPrefixes();
                if (prefixes != null) {
                    Iterator var6 = prefixes.iterator();

                    while(var6.hasNext()) {
                        String prefix = (String)var6.next();
                        if (parts == null && rawContent.toLowerCase().startsWith(prefix.toLowerCase())) {
                            parts = splitOnPrefixLength(rawContent, prefix.length());
                        }
                    }
                }
            }

            if (parts != null) {
                if (this.useHelp && parts[0].equalsIgnoreCase(this.helpWord)) {
                    CommandEvent cevent = new CommandEvent(event, parts[1] == null ? "" : parts[1], this);
                    if (this.listener != null) {
                        this.listener.onCommand(cevent, (Command)null);
                    }

                    this.helpConsumer.accept(cevent);
                    if (this.listener != null) {
                        this.listener.onCompletedCommand(cevent, (Command)null);
                    }

                    return;
                }

                if (event.isFromType(ChannelType.PRIVATE) || event.getTextChannel().canTalk()) {
                    String name = parts[0];
                    String args = parts[1] == null ? "" : parts[1];
                    Command command;
                    synchronized(this.commandIndex) {
                        int i = (Integer)this.commandIndex.getOrDefault(name.toLowerCase(), -1);
                        command = i != -1 ? (Command)this.commands.get(i) : null;
                    }

                    if (command != null) {
                        CommandEvent cevent = new CommandEvent(event, args, this);
                        if (this.listener != null) {
                            this.listener.onCommand(cevent, command);
                        }

                        this.uses.put(command.getName(), (Integer)this.uses.getOrDefault(command.getName(), 0) + 1);
                        command.run(cevent);
                        return;
                    }
                }
            }

            if (this.listener != null) {
                this.listener.onNonCommandMessage(event);
            }

        }
    }

    private void sendStats(JDA jda) {
        OkHttpClient client = jda.getHttpClient();
        Builder builder;
        if (this.carbonKey != null) {
            okhttp3.FormBody.Builder bodyBuilder = (new okhttp3.FormBody.Builder()).add("key", this.carbonKey).add("servercount", Integer.toString(jda.getGuilds().size()));
            if (jda.getShardInfo() != null) {
                bodyBuilder.add("shard_id", Integer.toString(jda.getShardInfo().getShardId())).add("shard_count", Integer.toString(jda.getShardInfo().getShardTotal()));
            }

            builder = (new Builder()).post(bodyBuilder.build()).url("https://www.carbonitex.net/discord/data/botdata.php");
            client.newCall(builder.build()).enqueue(new Callback() {
                public void onResponse(Call call, Response response) {
                    CommandClientImpl.LOG.info("Successfully send information to carbonitex.net");
                    response.close();
                }

                public void onFailure(Call call, IOException e) {
                    CommandClientImpl.LOG.error("Failed to send information to carbonitex.net ", e);
                }
            });
        }

        if (this.botsKey != null) {
            JSONObject body = (new JSONObject()).put("guildCount", jda.getGuilds().size());
            if (jda.getShardInfo() != null) {
                body.put("shardId", jda.getShardInfo().getShardId()).put("shardCount", jda.getShardInfo().getShardTotal());
            }

            builder = (new Builder()).post(RequestBody.create(MediaType.parse("application/json"), body.toString())).url("https://discord.bots.gg/api/v1/bots/" + jda.getSelfUser().getId() + "/stats").header("Authorization", this.botsKey).header("Content-Type", "application/json");
            client.newCall(builder.build()).enqueue(new Callback() {
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        CommandClientImpl.LOG.info("Successfully sent information to discord.bots.gg");

                        try {
                            Reader reader = response.body().charStream();
                            Throwable var4 = null;

                            try {
                                CommandClientImpl.this.totalGuilds = (new JSONObject(new JSONTokener(reader))).getInt("guildCount");
                            } catch (Throwable var14) {
                                var4 = var14;
                                throw var14;
                            } finally {
                                if (reader != null) {
                                    if (var4 != null) {
                                        try {
                                            reader.close();
                                        } catch (Throwable var13) {
                                            var4.addSuppressed(var13);
                                        }
                                    } else {
                                        reader.close();
                                    }
                                }

                            }
                        } catch (Exception var16) {
                            CommandClientImpl.LOG.error("Failed to retrieve bot shard information from discord.bots.gg ", var16);
                        }
                    } else {
                        CommandClientImpl.LOG.error("Failed to send information to discord.bots.gg: " + response.body().string());
                    }

                    response.close();
                }

                public void onFailure(Call call, IOException e) {
                    CommandClientImpl.LOG.error("Failed to send information to discord.bots.gg ", e);
                }
            });
        } else if (jda.getShardManager() != null) {
            this.totalGuilds = (int)jda.getShardManager().getGuildCache().size();
        } else {
            this.totalGuilds = (int)jda.getGuildCache().size();
        }

    }

    private void onMessageDelete(GuildMessageDeleteEvent event) {
        synchronized(this.linkMap) {
            if (this.linkMap.contains(event.getMessageIdLong())) {
                Set<Message> messages = (Set)this.linkMap.get(event.getMessageIdLong());
                if (messages.size() > 1 && event.getGuild().getSelfMember().hasPermission(event.getChannel(), new Permission[]{Permission.MESSAGE_MANAGE})) {
                    event.getChannel().deleteMessages(messages).queue((unused) -> {
                    }, (ignored) -> {
                    });
                } else if (messages.size() > 0) {
                    messages.forEach((m) -> {
                        m.delete().queue((unused) -> {
                        }, (ignored) -> {
                        });
                    });
                }
            }

        }
    }

    private GuildSettingsProvider provideSettings(Guild guild) {
        Object settings = this.getSettingsFor(guild);
        return settings != null && settings instanceof GuildSettingsProvider ? (GuildSettingsProvider)settings : null;
    }

    private static String[] splitOnPrefixLength(String rawContent, int length) {
        return (String[])Arrays.copyOf(rawContent.substring(length).trim().split("\\s+", 2), 2);
    }

    public void linkIds(long callId, Message message) {
        if (this.usesLinkedDeletion()) {
            synchronized(this.linkMap) {
                Set<Message> stored = (Set)this.linkMap.get(callId);
                if (stored != null) {
                    stored.add(message);
                } else {
                    Set<Message> stored2 = new HashSet();
                    stored2.add(message);
                    this.linkMap.add(callId, stored2);
                }

            }
        }
    }
}
