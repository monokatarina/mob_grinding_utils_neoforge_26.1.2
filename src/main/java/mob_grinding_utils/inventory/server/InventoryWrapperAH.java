package mob_grinding_utils.inventory.server;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nonnull;

public class InventoryWrapperAH implements ResourceHandler<ItemResource> {

	private final Container inv;

	public InventoryWrapperAH(Container inv) {
		this.inv = inv;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		InventoryWrapperAH that = (InventoryWrapperAH) o;
		return getInv().equals(that.getInv());
	}

	@Override
	public int hashCode() {
		return getInv().hashCode();
	}

	// ===== MÉTODOS DO ResourceHandler =====

	@Override
	public int size() {
		return inv.getContainerSize();
	}

	@Override
	public ItemResource getResource(int index) {
		ItemStack stack = inv.getItem(index);
		if (stack.isEmpty()) {
			return ItemResource.EMPTY;
		}
		return ItemResource.of(stack);
	}

	@Override
	public long getAmountAsLong(int index) {
		return inv.getItem(index).getCount();
	}

	@Override
	public long getCapacityAsLong(int index, ItemResource resource) {
		// Se o slot não aceitar o item, capacidade é 0
		if (!isValid(index, resource)) {
			return 0;
		}
		ItemStack existing = inv.getItem(index);
		if (existing.isEmpty()) {
			return Math.min(resource.getMaxStackSize(), 64);
		}
		// Se o slot já tem o mesmo item, retorna a capacidade total (max stack)
		if (resource.matches(existing)) {
			return Math.min(resource.getMaxStackSize(), 64);
		}
		return 0;
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		// Slot 0 é de upgrade, não aceita inserção
		if (index == 0) return false;
		if (resource.isEmpty()) return false;
		return inv.canPlaceItem(index, resource.toStack());
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

		// Slot 0 não aceita inserção
		if (index == 0) return 0;
		if (!inv.canPlaceItem(index, resource.toStack())) return 0;

		ItemStack existing = inv.getItem(index);
		ItemStack stackToInsert = resource.toStack(amount);

		if (existing.isEmpty()) {
			// Slot vazio, pode inserir
			int maxStack = Math.min(resource.getMaxStackSize(), 64);
			int toInsert = Math.min(amount, maxStack);

			// transaction.depth() == 0 significa que é uma simulação (não modifica)
			// ou podemos usar o padrão de verificar se é transiente
			if (transaction.depth() == 0) {
				// É uma simulação, não modifica
				return toInsert;
			} else {
				// É uma operação real
				ItemStack newStack = resource.toStack(toInsert);
				inv.setItem(index, newStack);
				inv.setChanged();
				return toInsert;
			}

		} else if (resource.matches(existing)) {
			// Slot já tem o mesmo item
			int maxStack = Math.min(resource.getMaxStackSize(), 64);
			int space = maxStack - existing.getCount();
			int toInsert = Math.min(amount, space);

			if (toInsert > 0) {
				if (transaction.depth() > 0) {
					// Operação real
					existing.grow(toInsert);
					inv.setChanged();
				}
				// Se depth == 0, é simulação, retorna o valor sem modificar
			}
			return toInsert;
		}

		return 0; // Slot ocupado com item diferente
	}

	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

		// Slot 0 não pode ser extraído
		if (index == 0) return 0;

		ItemStack existing = inv.getItem(index);
		if (existing.isEmpty()) return 0;
		if (!resource.matches(existing)) return 0;

		int toExtract = Math.min(amount, existing.getCount());

		if (transaction.depth() > 0) {
			// Operação real
			existing.shrink(toExtract);
			if (existing.isEmpty()) {
				inv.setItem(index, ItemStack.EMPTY);
			}
			inv.setChanged();
		}
		// Se depth == 0, é simulação, retorna o valor sem modificar

		return toExtract;
	}

	// ===== MÉTODOS ADICIONAIS =====

	public Container getInv() {
		return inv;
	}

	/**
	 * Verifica se um item pode ser inserido em um slot específico
	 */
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		if (slot == 0) return false;
		return inv.canPlaceItem(slot, stack);
	}
}
