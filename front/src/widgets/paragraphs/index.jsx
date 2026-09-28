import { useState } from "react";
import { Button } from "react-bootstrap";
import "./styles.css";

const getParagraphStyle = (isInverse, index) => {
    if (!isInverse) {
        return index % 2 === 0 ? "even" : "odd";
    } else {
        return index % 2 === 0 ? "odd" : "even";
    }
};

export const Paragraphs = () => {
    const [data, setData] = useState(null);
    const [inverse, setInverse] = useState(false);

    const handleParagraphClick = (index) => {
        console.debug(index);
    };

    const handleClick = (handleClick = null) => {
        setData({ count: 10, handler: handleClick });
    };

    const handleClick1 = () => {
        handleClick();
    };

    const handleClick2 = () => {
        handleClick(handleParagraphClick);
    };

    const handleClickInverse = () => {
        setInverse(!inverse);
    };

    const paragraphs = [...Array(data?.count ?? 0).keys()].map((item) => {
        const index = item + 1;
        const cursor = data.handler !== null ? "myp" : "";
        const style = getParagraphStyle(inverse, index);
        const classes = [cursor, style].join(" ");
        const handleClick = data.handler ? () => data.handler(index) : null;
        return (
            <p key={index} className={classes} onClick={handleClick}>
                Paragraph #{index}
            </p>
        );
    });

    return (
        <>
            <div className="row row-cols-1 justify-content-center">
                <div className="col col-md-6 col-lg-4 col-xl-2 m-lg-0 mb-2">
                    <Button className="d-block m-auto w-100" onClick={handleClick1}>
                        Создать 1
                    </Button>
                </div>
                <div className="col col-md-6 col-lg-4 col-xl-2 m-lg-0 mb-2">
                    <Button className="d-block m-auto w-100" onClick={handleClick2}>
                        Создать 2
                    </Button>
                </div>
                <div className="col col-md-6 col-lg-4 col-xl-2 m-lg-0 mb-2">
                    <Button id="inverse" className="d-block m-auto w-100" onClick={handleClickInverse}>
                        Инвертировать
                    </Button>
                </div>
            </div>
            <div className="row justify-content-center mt-2">
                <div className="col col-md-6">{paragraphs}</div>
            </div>
        </>
    );
};
