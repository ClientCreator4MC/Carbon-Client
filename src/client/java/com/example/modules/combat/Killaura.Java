import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class CombatUtils {

    public static void attackEntity(Entity target) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || target == null || !target.isAlive()) return;

        // Rotate player to face the target
        faceEntity(player, target);

        // Swing arm (client-side and server-side)
        player.swingHand(Hand.MAIN_HAND);
        client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        // Send attack packet to the server
        PlayerInteractEntityC2SPacket attackPacket = PlayerInteractEntityC2SPacket.attack(target, player.isSneaking());
        client.getNetworkHandler().sendPacket(attackPacket);
    }

    public static void faceEntity(ClientPlayerEntity player, Entity target) {
        Vec3d targetPos = target.getEyePos();
        Vec3d playerPos = player.getEyePos();

        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distXZ));

        player.setYaw(yaw);
        player.setPitch(pitch);
    }
}
