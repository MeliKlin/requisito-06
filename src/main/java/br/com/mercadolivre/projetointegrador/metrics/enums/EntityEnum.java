package br.com.mercadolivre.projetointegrador.metrics.enums;

import br.com.mercadolivre.projetointegrador.marketplace.enums.PurchaseStatusCodeEnum;

public enum EntityEnum {

    ad,
    batch,
    both;

    public static boolean contains(String entity) {
        for (PurchaseStatusCodeEnum c : PurchaseStatusCodeEnum.values()) {
            if (c.name().equals(entity)) {
                return true;
            }
        }

        return false;
    }

}
