import { Banner } from "@widgets/index";
import { Link } from "react-router-dom";
import "./styles.css";

export const MainPage = () => {
    return (
        <>
            <Banner className="mb-4" />
            <section className="content">
                <h1>Пример web-страницы</h1>
                <h2>1. Структурные элементы</h2>
                <p>
                    <b>
                        Полужирное начертание <i>курсив</i>
                    </b>
                </p>
                <p>
                    Абзац 2 <Link to="/page2">Ссылка</Link>
                </p>
                <h3>1.1. Списки</h3>
                <p>Список маркированный:</p>
                <ul>
                    <li>
                        <Link to="/page2" target="_blank">
                            Элемент списка 1
                        </Link>
                    </li>
                    <li>Элемент списка 2</li>
                    <li>...</li>
                </ul>
                <p>Список нумерованный:</p>
                <ol>
                    <li>Элемент списка 1</li>
                    <li>Элемент списка 2</li>
                    <li>...</li>
                </ol>
                <img
                    className="d-block d-md-inline float-md-start mb-4 me-md-2 my-md-2 illustration"
                    src="https://ulstu.ru/upload/iblock/1b4/7h8wjum3zmw61bjvb31s6gacil6mw6wq/ulgtu.jpg"
                    alt="Main"
                />
                <p>
                    Lorem ipsum odor amet, consectetuer adipiscing elit. Mus nisl sociosqu sapien, suspendisse enim
                    laoreet et. Taciti adipiscing cras ipsum libero fames mollis. Sociosqu aliquet a taciti ridiculus
                    tincidunt dolor dui malesuada rutrum. Maecenas tellus quis suscipit proin egestas. Risus nostra erat
                    porttitor habitasse platea donec quisque conubia. Consequat tempus est interdum leo et cras potenti
                    ullamcorper.
                </p>
                <p>
                    Dictum iaculis enim nullam luctus hac tincidunt suscipit dictum convallis. Suscipit id iaculis
                    venenatis purus conubia lacinia suspendisse donec. Non adipiscing ultricies potenti euismod dapibus;
                    quis morbi. Himenaeos eros elit non duis nullam ante dictum etiam platea. Taciti nunc nostra mi urna
                    turpis nulla per congue. Mus vestibulum proin suscipit iaculis facilisis. Magnis in laoreet tempus
                    quis dictum quis quam. Et interdum posuere pulvinar torquent vulputate lacus. Augue facilisis
                    sodales lacinia leo ligula!
                </p>
                <p>
                    Et placerat vehicula malesuada aptent fringilla sit ullamcorper maximus. Non pharetra morbi inceptos
                    quis tellus aenean magnis aenean. Metus ante tincidunt himenaeos suspendisse arcu curabitur cubilia.
                    Maximus aliquet odio potenti ex; class dapibus euismod. Pharetra sed praesent placerat efficitur
                    dolor enim nisi. Maximus est suspendisse semper phasellus; cubilia luctus feugiat netus. Maecenas
                    dis donec varius sapien tincidunt augue lectus. Porttitor id dui commodo vitae ad vivamus pulvinar.
                </p>
                <p>
                    Consectetur aptent montes lacinia egestas augue nunc integer. Fames purus risus non rhoncus arcu
                    quisque justo eu. Elementum natoque dapibus bibendum euismod justo fames velit. Venenatis senectus
                    nisl odio facilisis laoreet fusce nibh at sollicitudin? Feugiat primis mus curae nostra tempor
                    libero morbi ultrices dolor. Adipiscing class ligula vivamus eu facilisi non. Ipsum congue nascetur
                    rhoncus dictumst varius quam risus in sem.
                </p>
                <p>
                    Quam bibendum commodo a curae neque lacus. Mauris semper netus ridiculus ac cursus malesuada turpis
                    tristique? Dui proin sit pharetra natoque gravida scelerisque. Donec mollis aliquam, venenatis
                    bibendum mattis urna sed. Vel vel eleifend est justo efficitur augue habitant. At cras phasellus
                    sapien torquent suscipit ex. Amet ornare mauris cubilia ullamcorper aptent molestie sem a. Curae
                    dolor habitant id molestie ut fames orci. Per nec mattis, hendrerit aliquam mus enim aliquet.
                </p>
            </section>
        </>
    );
};
