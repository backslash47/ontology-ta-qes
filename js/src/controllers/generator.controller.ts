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
import axios from 'axios';
import { Request as OntRequest, CONST} from 'ont-sdk-ts';
import config from '../config';
import { generateChallenge } from '../services/generator';

const router: Router = Router();

router.post('/', async (req: Request, res: Response) => {
    const body = req.body;
    const request = OntRequest.deserialize(body);

    // verifies if the request subject is TA
    if (request.metadata.subject !== config.ont.id) {
        console.log('Wrong subject.');
        res.sendStatus(403);
    }

    // verifies the request signature
    const verifyResult = await request.verify(CONST.TEST_ONT_URL.REST_URL);
    if (!verifyResult) {
        console.log('Wrong signature.');
        res.sendStatus(403);
    }
    
    // generates challenge
    const ontId = request.metadata.issuer;
    const challenge = await generateChallenge(ontId);
    
    // posts the challenge to java to generate pdf
    const result = await axios.post('http://localhost:8080/generate', challenge, {
        responseType: 'stream'
    });

    // pipes the result back to client
    result.data.pipe(res);
});

export const GeneratorController: Router = router;
