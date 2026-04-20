const API_URL = "http://localhost:8080/api/hello-world";

async function loadHelloWorld() {
  try {
    const postResponse = await fetch(API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    }).then((response) => response.json());

    const getResponse = await fetch(API_URL, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }).then((response) => response.json());

    const postData = postResponse;
    const getData = getResponse;

    const div = document.createElement("div");

    div.innerHTML = `
			<pre>Via POST: ${postData.message}</pre>
			<pre>Via GET: ${getData.message}</pre>
		`;

    document.querySelector("body").appendChild(div);
  } catch (e) {
    console.log("error", e);
  }
}

window.onload = loadHelloWorld;
