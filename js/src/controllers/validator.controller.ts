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

import { Router, Request, Response } from 'express';
import { post } from 'request-promise-native';
import { Request as OntRequest, CONST} from 'ont-sdk-ts';
import config from '../config';
import { generateClaim } from '../services/validator';

const router: Router = Router();

router.post('/', async (req: Request, res: Response) => {
    
    // pipes the challenge to java to get the jwt challenge and parsed data from signature
    const rq = post('http://localhost:8080/validate');
    req.pipe(rq);
    const result = await rq;
    const { jwt, fullName, country } = JSON.parse(result);
    
    const request = OntRequest.deserialize(jwt);

    // verifies if the challenge issuer is TA
    if (request.metadata.issuer !== config.ont.id) {
        console.log('Wrong issuer.');
        res.sendStatus(403);
    }

    // verifies the challenge signature
    const verifyResult = await request.verify(CONST.TEST_ONT_URL.REST_URL);
    if (!verifyResult) {
        console.log('Wrong signature.');
        res.sendStatus(403);
    }

    // generates and attests claim
    const claim = await generateClaim(request.metadata.subject, fullName, country);
    
    // returns the verified claim to user
    res.set({"Content-Disposition":"attachment; filename=\"claim.jwt\""});
    res.setHeader('Content-type', 'application/octet-stream');
    res.send(claim);
});

export const ValidatorController: Router = router;
