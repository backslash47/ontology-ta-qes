/*
 * Copyright (C) 2018 Matus Zamborsky
 * This file is part of The ONT Detective.
 *
 * The ONT Detective is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ONT Detective is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with The ONT Detective.  If not, see <http://www.gnu.org/licenses/>.
 */

import { crypto, CONST, Claim } from 'ont-sdk-ts';
import config from '../config';
import PrivateKey = crypto.PrivateKey; 

/**
 * Generates and attest claim for the user.
 * 
 * @param subjectId ONT ID of the user
 * @param email Email of the user
 */
export async function generateClaim(subjectId: string, fullName: string, country: string): Promise<string> {
    const privateKey = new PrivateKey(config.ont.privateKey);

    const claim = new Claim({
        issuer: config.ont.id,
        subject: subjectId,
        issuedAt: Math.floor(Date.now() / 1000)
    }, undefined);
    claim.context = 'claim:qes-identity';
    claim.content = { fullName, country };

    await claim.sign(CONST.TEST_ONT_URL.REST_URL, config.ont.publicKeyId, privateKey);
    await claim.attest(CONST.TEST_ONT_URL.SOCKET_URL, privateKey);

    return claim.serialize();
}
