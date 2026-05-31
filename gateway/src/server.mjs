import {config} from "./config.mjs";
import {createApp} from "./app.mjs";

const app = createApp();

app.listen(config.port, () => {
    console.log(`[POC Gateway] Running on http://localhost:${config.port}`);
});
