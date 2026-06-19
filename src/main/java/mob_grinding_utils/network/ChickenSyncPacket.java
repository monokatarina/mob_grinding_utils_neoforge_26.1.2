package mob_grinding_utils.network;

import mob_grinding_utils.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ChickenSyncPacket(
		int chickenID,
		CompoundTag nbt
) implements CustomPacketPayload {

	public static final Type<ChickenSyncPacket> TYPE =
			new Type<>(
					Identifier.fromNamespaceAndPath(
							Reference.MOD_ID,
							"chicken_sync"
					)
			);

	public static final StreamCodec<
			FriendlyByteBuf,
			ChickenSyncPacket
			> STREAM_CODEC = CustomPacketPayload.codec(
			ChickenSyncPacket::write,
			ChickenSyncPacket::new
	);

	public ChickenSyncPacket(
			LivingEntity chicken,
			CompoundTag chickenNBT
	) {
		this(
				chicken.getId(),
				chickenNBT
		);
	}

	public ChickenSyncPacket(FriendlyByteBuf buffer) {
		this(
				buffer.readInt(),
				buffer.readNbt()
		);
	}

	public static void handle(
			ChickenSyncPacket message,
			IPayloadContext context
	) {
		context.enqueueWork(
				() -> MGUClientPackets.handleChickenSync(message)
		);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(chickenID);
		buffer.writeNbt(nbt);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
