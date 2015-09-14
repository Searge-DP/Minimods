package me.guichaguri.villagerlanterns;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author Guilherme Chaguri
 */
public class LanternTransformer implements IClassTransformer {
    private final String CLASS_DEOB = "net.minecraft.world.gen.structure.StructureVillagePieces$Torch";
    private final String CLASS_OB = "avt";
    private final String METHOD_DEOB = "addComponentParts";
    private final String METHOD_OB = "a"; //func_74875_a
    private final String DESC_DEOB = "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z";
    private final String DESC_OB = "(Lahb;Ljava/util/Random;Lasv;)Z";

    private final String FILL_DEOB = "fillWithBlocks";
    private final String FILL_OB = "a"; // func_151549_a
    private final String FILL_DESC_DEOB = "(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;IIIIIILnet/minecraft/block/Block;Lnet/minecraft/block/Block;Z)V";
    private final String FILL_DESC_OB = "(Lahb;Lasv;IIIIIILaji;Laji;Z)V";

    private final String GETX_DEOB = "getXWithOffset";
    private final String GETX_OB = "a";
    private final String GETZ_DEOB = "getZWithOffset";
    private final String GETZ_OB = "b";
    private final String GETY_DEOB = "getYWithOffset";
    private final String GETY_OB = "a";

    private final String GETXZ_DESC = "(II)I";
    private final String GETY_DESC = "(I)I";

    private final String BUILD_DEOB_DESC = "(Lnet/minecraft/world/World;III)V";
    private final String BUILD_OB_DESC = "(Lahb;III)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        if(check(name, CLASS_OB, CLASS_DEOB)) {
            System.out.println("PATCH1");
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);
            for(MethodNode method : classNode.methods) {
                if(check(method.name, METHOD_OB, METHOD_DEOB) && check(method.desc, DESC_OB, DESC_DEOB)) {

                    InsnList list = new InsnList();
                    for(AbstractInsnNode node : method.instructions.toArray()) {
                        list.add(node);
                        if(node instanceof MethodInsnNode) {
                            MethodInsnNode methodNode = (MethodInsnNode)node;
                            if(methodNode.name.equals(FILL_OB) && methodNode.desc.equals(FILL_DESC_OB)) {
                                addInstructions(list, classNode, false);
                            } else if(methodNode.name.equals(FILL_DEOB) && methodNode.desc.equals(FILL_DESC_DEOB)) {
                                addInstructions(list, classNode, true);
                            }
                        }
                    }
                    method.instructions.clear();
                    method.instructions.add(list);

                }
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return bytes;
    }

    private void addInstructions(InsnList list, ClassNode classNode, boolean deob) {
        list.add(new VarInsnNode(Opcodes.ALOAD, 1)); // World
        list.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
        list.add(new InsnNode(Opcodes.ICONST_1)); // 1
        list.add(new InsnNode(Opcodes.ICONST_0)); // 0
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, deob ? GETX_DEOB : GETX_OB, GETXZ_DESC, false));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
        list.add(new InsnNode(Opcodes.ICONST_0)); // 0
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, deob ? GETY_DEOB : GETY_OB, GETY_DESC, false));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
        list.add(new InsnNode(Opcodes.ICONST_1)); // 1
        list.add(new InsnNode(Opcodes.ICONST_0)); // 0
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, deob ? GETZ_DEOB : GETZ_OB, GETXZ_DESC, false));

        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/guichaguri/villagerlanterns/LanternHooks",
                                    "buildLantern", deob ? BUILD_DEOB_DESC : BUILD_OB_DESC, false));

        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.IRETURN));
    }

    private boolean check(String name, String n1, String n2) {
        if(name.equals(n1)) return true;
        if(name.equals(n2)) return true;
        return false;
    }
}
