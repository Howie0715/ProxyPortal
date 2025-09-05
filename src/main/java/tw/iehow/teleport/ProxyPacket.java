package tw.iehow.teleport;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public record ProxyPacket(String... args) implements CustomPayload {
    public static final Identifier BUNGEECORD_ID = Identifier.of("bungeecord", "main");
    public static final CustomPayload.Id<ProxyPacket> PACKET_ID = new CustomPayload.Id<>(BUNGEECORD_ID);
    public static final PacketCodec<RegistryByteBuf, ProxyPacket> codec = new PacketCodec<>() {

        @Override
        public ProxyPacket decode(RegistryByteBuf buf) {
            return null;
        }

        @Override
        public void encode(RegistryByteBuf buf, ProxyPacket value) {
            try (ByteBufDataOutput output = new ByteBufDataOutput(new PacketByteBuf(Unpooled.buffer()))) {
                for (String arg : value.args())
                    output.writeUTF(arg);
                buf.writeBytes(output.getBuf());

            } catch (Exception ignored) {
            }
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    private static class ByteBufDataOutput extends OutputStream {
        private final PacketByteBuf packetByteBuf;
        private final DataOutputStream dataOutputStream;

        public ByteBufDataOutput(PacketByteBuf buf) {
            this.packetByteBuf = buf;
            this.dataOutputStream = new DataOutputStream(this);
        }

        public PacketByteBuf getBuf() {
            return packetByteBuf;
        }

        @Override
        public void write(int b) {
            packetByteBuf.writeByte(b);
        }

        public void writeUTF(String s) {
            try {
                this.dataOutputStream.writeUTF(s);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}