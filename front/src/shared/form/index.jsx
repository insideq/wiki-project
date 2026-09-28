import { useState } from "react";
import { useForm } from "react-hook-form";

export const useBSForm = (initState, stayValidated = true) => {
    const { register, handleSubmit, setValue, reset, resetField, setFocus } = useForm({ defaultValues: initState });
    const [validated, setValidated] = useState(false);

    const handleSubmitWrapper = (event, onSubmit) => {
        event.preventDefault();
        event.stopPropagation();

        const form = event.currentTarget;
        if (form.checkValidity()) {
            handleSubmit(onSubmit)(event);
        }

        setValidated(stayValidated ? true : !form.checkValidity());
    };

    return { register, validated, handleSubmit: handleSubmitWrapper, setValue, resetField, reset, setFocus };
};
